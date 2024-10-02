package com.example.furnature.controller;

import java.io.IOException;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.furnature.dao.PaymentServiceImpl;
import com.google.gson.Gson;
import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class PaymentController {
	@Autowired
	PaymentServiceImpl paymentService;
	
	//private IamportClient iamportClient;
	
	// 결제
	@PostMapping("/payment/payment/{imp_uid}")
	public IamportResponse<Payment> paymentByImpUid(Model model, @RequestParam HashMap<String, Object> map) throws Exception {
		System.out.println(map);
		return paymentService.payment(map);	
	}
	
	// 결제 취소
	@PostMapping("/payment/cancel/{imp_uid}")
	public IamportResponse<Payment> cancelPaymentByImpUid(Model model, @RequestParam HashMap<String, Object> map) throws Exception {
		System.out.println(map);
		return paymentService.cancel(map);	
	}
	
	// 결제 내역 등록
	@RequestMapping(value = "/payment/payment-add.dox", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String addPayment(Model model, @RequestParam HashMap<String, Object> map) throws Exception {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		resultMap = paymentService.addPayment(map);
		return new Gson().toJson(resultMap);
	}
	
	// 결제 취소 전 정보 불러오기
	@RequestMapping(value = "/payment/payment-info.dox", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String searchPayment(Model model, @RequestParam HashMap<String, Object> map) throws Exception {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		resultMap = paymentService.searchPaymentInfo(map);
		return new Gson().toJson(resultMap);
	}
	
	// 결제 내역 수정 - 결제 취소
	@RequestMapping(value = "/payment/payment-edit.dox", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String editPayment(Model model, @RequestParam HashMap<String, Object> map) throws Exception {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		resultMap = paymentService.editPayment(map);
		return new Gson().toJson(resultMap);
	}
	
	
	private IamportClient iamportClient;
	public PaymentController() {
        this.iamportClient = new IamportClient("2547521225544270", "m9t32DK2cjfLX6Fo2NUVcAsQySGqEO1GUBbnpXX1mUBHyxUGE0qqiSopsGbPwsSmYyfHjFrYs79ajDuw");
    }

//    
    @PostMapping("/cancel/{imp_uid}")
    private IamportResponse<Payment> cancelPaymentByImpUid(@PathVariable("imp_uid") String imp_uid) throws IamportResponseException, IOException {
        return iamportClient.cancelPaymentByImpUid(new CancelData(imp_uid, true));
    }
}
