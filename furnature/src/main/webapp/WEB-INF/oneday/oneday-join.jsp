<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
	<jsp:include page="/layout/headlink.jsp"></jsp:include>
	<script src="https://cdn.iamport.kr/v1/iamport.js"></script>

	<style>
		img{
			width:600px;
		}
	</style>	
</head>
<body>
	<jsp:include page="/layout/header.jsp"></jsp:include>
	<div id="app">
		<div class="slider">
		<div class="slide" v-for="(file, index) in filePath" :key="file" v-show="currentSlide === index">
           <img :src="file">
       </div>
	   </div>
	   <button @click="prevSlide"><</button>
	   {{currentSlide+1}}
	   <button @click="nextSlide">></button>
		  
		<div>{{classNo}}</div>
		<div>{{className}}</div>
		<div>모집 시작일 : {{startDay}}</div>
		<div>모집 종료일 : {{endDay}}</div>
		<div>수업일자 : {{classDate}}</div>
		<div>수강료 : {{price}}</div>
		<div class="onedayJoinForm" v-if="message=='' && numberLimit>currentNumber">
			신청자 이름 : <input type="text" v-model="name">
		</div>
		<div v-if="message">{{message}}</div>
		<div v-if="message2">{{message2}}</div>
		<div v-if="message=='' && numberLimit>currentNumber"><button @click="fnOnedayJoin">수강신청</button></div>
		
		<button v-if="isAdmin" @click="fnUpdate(detail[0].classNo)">수정</button>
		<button v-if="isAdmin" @click="fnDelete(detail[0].classNo)">삭제</button>
    </div>
	<jsp:include page="/layout/footer.jsp"></jsp:include>
</body>
</html>
<script>
	const userCode = ""; 
	IMP.init("imp52370275");
	//포트원 결제 api 사용
    const app = Vue.createApp({
            data() {
                return {
                   classNo : "${classNo}",
				   className : "",
				   detail : {},
				   filePath : [],
				   userId : "${sessionId}",
				   name : "",
				   count : "",
				   price : "",
				   payId : "",
				   numberLimit : "",
				   currentNumber : "",
				   isAdmin : false,
				   sessionAuth: "${sessionAuth}",
				   today : new Date(),
				   startDay : "",
				   endDay : "",
				   classDate : "",
				   message : "",
				   message2 : "",
				   currentSlide: 0,
				   alreadyIn : false
                };
            },
            methods: {
				nextSlide() {
				        if (this.currentSlide < this.filePath.length - 1) {
				            this.currentSlide++;
				        } else {
				            this.currentSlide = 0; // 처음으로 돌아가기
				        }
				},
			    prevSlide() {
			        if (this.currentSlide > 0) {
			            this.currentSlide--;
			        } else {
			            this.currentSlide = this.filePath.length - 1; // 마지막으로 돌아가기
			        }
			    },
				fnDetail(classNo) {
					var self = this;
					var nparmap = {classNo:self.classNo};
					
					$.ajax({
					url : "/oneday/oneday-detail.dox",
					dataType : "json",
					type : "POST",
					data : nparmap,
					success : function(data){
						console.log(data);
						self.detail = data.onedayDetail; 
						self.classNo = data.onedayDetail[0].classNo;
						self.className = data.onedayDetail[0].className;
						self.startDay = data.onedayDetail[0].startDay;
						self.endDay = data.onedayDetail[0].endDay;
						self.classDate = data.onedayDetail[0].classDate;
						self.price = data.onedayDetail[0].price;
						
						self.filePath = [];			
						self.detail.forEach(item => {
                           if (item.filePath) {
                               self.filePath.push(item.filePath); 
                           }
                       });
							
						var endDay = new Date(self.detail[0].endDay);
						var today = new Date()
						if(endDay<today){
							self.message = "모집일자가 지났습니다.";
						}else{
							self.message = "";
						}
						var nparmap = {classNo:self.classNo};
						$.ajax({
							url : "/oneday/oneday-numberLimit.dox",
							dataType : "json",
							type : "POST",
							data : nparmap,
							success : function(data){
								console.log(data);
								self.numberLimit = data.numberLimit.numberLimit;
								self.currentNumber = data.numberLimit.currentNumber;
								if(self.numberLimit==self.currentNumber){
									self.message2 = "모집 인원이 초과되었습니다.";
									return;
								}else{
									self.message2 = "";
								}
							}							
						})
						
					}
					
				})
				},
			   fnOnedayJoin(){
					var self = this;
					if(self.userId==''){
						alert("수강 신청은 로그인 후 가능합니다");
						return;
					}	
					if(self.name==''){
						alert("이름을 입력해주세요");
						return;
					}
					var nparmap = {classNo:self.classNo};
					$.ajax({
						url : "/oneday/oneday-numberLimit.dox",
						dataType : "json",
						type : "POST",
						data : nparmap,
						success : function(data){
							self.numberLimit = data.numberLimit;
							var payConfirm = confirm("결제하시겠습니까?");
							if(payConfirm){
								self.fnPay();						
							}else{
								alert("결제를 취소하셨습니다");
							}	
						}							
					})				
			   },
				fnPay() {
				    var self = this;
				    IMP.request_pay({
						pg: "html5_inicis",
					    pay_method: "card",
						merchant_uid: 'oneday' + new Date().getTime(),
					    name: self.detail[0].className,
					    amount: self.price,
					    buyer_tel: "010-0000-0000",
					  }	, function (rsp){ // callback
				        if (rsp.success) {
				            alert("성공");
				            console.log(rsp);
				            self.fnSave(rsp);
				        } else {
				            alert("실패");
				        }
				    }); 
				},
	
				fnSave(rsp) {
				    var self = this;
				    var nparmap = {name : self.name, price : rsp.paid_amount, payId : rsp.merchant_uid, classNo:self.classNo, userId:self.userId};
				    $.ajax({
				        url: "/oneday/oneday-pay.dox",
				        dataType: "json",
				        type: "POST",
				        data: nparmap,
				        success: function (data) {
							if (data.result === "success") {
	                            alert("신청이 완료되었습니다.");
	                        } else {
	                            alert("신청 중 문제가 발생했습니다.");
	                        }
				        }
				    }); 
				},
				
				fnUpdate(classNo){
					var self = this;						
					$.pageChange("/oneday/oneday-update.do", {classNo:self.classNo});
				},
				fnDelete(classNo) {
					var self = this;
				    var nparmap = { classNo: classNo };
				    $.ajax({
				        url: "/admin/oneday-delete.dox",
				        dataType: "json",
				        type: "POST",
				        data: nparmap,
				        success: function(data) {
				         	console.log(data);
				        }
				    });
				}
			},
            mounted() {
				var self = this;
				self.fnDetail(self.classNo);
				if (self.sessionAuth === "2") {
				       self.isAdmin = true;
				   }
            }
        });
        app.mount('#app');
</script>