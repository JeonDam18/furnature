package com.example.furnature.controller;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.furnature.dao.OnedayService;
import com.google.gson.Gson;

@Controller
public class OnedayController {
	
	@Autowired
	OnedayService onedayService;
	
	//원데이클래스 목록출력
	@RequestMapping("/oneday/oneday.do")
	 public String onedayList(Model model) throws Exception{

        return "/oneday/oneday";
    }
	
	//원데이클래스 수강신청
	@RequestMapping("/oneday/oneday-join.do")
	 public String onedayClassJoin(HttpServletRequest request, Model model, @RequestParam HashMap<String,Object> map) throws Exception{
		request.setAttribute("classNo", map.get("classNo"));
       return "/oneday/oneday-join";
   }
	
	//원데이클래스 등록(관리자)
	@RequestMapping("/oneday/oneday-register.do")
	 public String onedayFile(Model model) throws Exception{
		
		return "/oneday/oneday-register";
 }
	//원데이클래스 수정(관리자)
	@RequestMapping("/oneday/oneday-update.do")
	 public String update(HttpServletRequest request, Model model, @RequestParam HashMap<String,Object> map) throws Exception{
		request.setAttribute("classNo", map.get("classNo"));
		return "/oneday/oneday-update";	
}
	
	//원데이클래스 목록출력
	@RequestMapping(value = "/oneday/oneday-list.dox", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String onedayList(Model model, @RequestParam HashMap<String, Object> map) throws Exception {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		resultMap = onedayService.onedayList(map);
		return new Gson().toJson(resultMap);
	}
	
	//원데이클래스 상세내역(각 클래스 정보 개별 출력)
	@RequestMapping(value = "/oneday/oneday-detail.dox", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String onedayDetail(Model model, @RequestParam HashMap<String, Object> map) throws Exception {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		resultMap = onedayService.onedayDetail(map);
		return new Gson().toJson(resultMap);
	}
	
	//원데이클래스 수강신청
	@RequestMapping(value = "/oneday/oneday-join.dox", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String onedayJoin(HttpServletRequest request, Model model, @RequestParam HashMap<String, Object> map) throws Exception {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		resultMap = onedayService.onedayJoin(map);
		return new Gson().toJson(resultMap);
	}
	
	//원데이클래스 등록(관리자)
	@RequestMapping(value = "/oneday/oneday-register.dox", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String onedayReg(HttpServletRequest request, Model model, @RequestParam HashMap<String, Object> map) throws Exception {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		resultMap = onedayService.onedayReg(map);
		return new Gson().toJson(resultMap);
	}
	
	//원데이클래스 파일등록(관리자)
	@RequestMapping(value = "/oneday/oneday-file.dox")
	 public String result(@RequestParam("file") MultipartFile[] multi, @RequestParam("classNo") int classNo, HttpServletRequest request,HttpServletResponse response, Model model)
    {
        String url = null;
        String path=System.getProperty("user.dir");
        try {
 
            //String uploadpath = request.getServletContext().getRealPath(path);
            String filePath = path;
            for(MultipartFile files : multi) {
            	if(!files.isEmpty()) {
	            	String originFilename = files.getOriginalFilename();
	                String extName = originFilename.substring(originFilename.lastIndexOf("."),originFilename.length());
	                long size = files.getSize();
	                String saveFileName = genSaveFileName(extName);	
	                
	                File saveFile = new File(path + "\\src\\main\\webapp\\uploadImages\\oneday", saveFileName);
	                files.transferTo(saveFile);
	                
	                HashMap<String, Object> map = new HashMap<String, Object>();
	                map.put("fileName", saveFileName);
	                map.put("filePath", "../uploadImages/oneday/" + saveFileName);
	                map.put("fileSize", size);
	                map.put("extName", extName);
	                map.put("classNo", classNo);
	                onedayService.onedayFile(map);
	                // insert 쿼리 실행         
            }
       
            }      
//                model.addAttribute("fileName", multi.getOriginalFilename());
//                model.addAttribute("filePath", file.getAbsolutePath());
                
                return "redirect:/oneday/oneday.do";
            
        }catch(Exception e) {
            System.out.println(e);
        }
        return "redirect:/oneday/oneday.do";
    }
    
    // 현재 시간을 기준으로 파일 이름 생성
    private String genSaveFileName(String extName) {
        String fileName = "";
        
        Calendar calendar = Calendar.getInstance();
        fileName += calendar.get(Calendar.YEAR);
        fileName += calendar.get(Calendar.MONTH);
        fileName += calendar.get(Calendar.DATE);
        fileName += calendar.get(Calendar.HOUR);
        fileName += calendar.get(Calendar.MINUTE);
        fileName += calendar.get(Calendar.SECOND);
        fileName += calendar.get(Calendar.MILLISECOND);
        fileName += extName;
        
        return fileName;
    }
    
    //원데이클래스 인원초과 여부 확인
    @RequestMapping(value = "/oneday/oneday-numberLimit.dox", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
  	@ResponseBody
  	public String numberLimit(HttpServletRequest request, Model model, @RequestParam HashMap<String, Object> map) throws Exception {
  		HashMap<String, Object> resultMap = new HashMap<String, Object>();
  		resultMap = onedayService.numberLimit(map);
  		return new Gson().toJson(resultMap);
  	}
    
    //원데이클래스 수정(관리자)
    @RequestMapping(value = "/oneday/oneday-update.dox", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String onedayUpdate(HttpServletRequest request, Model model, @RequestParam HashMap<String, Object> map) throws Exception {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		resultMap = onedayService.onedayUpdate(map);
		return new Gson().toJson(resultMap);
	}
    
    //원데이클래스 중복신청 여부
    @RequestMapping(value = "/oneday/oneday-check.dox", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
	@ResponseBody
	public String onedayCheck(HttpServletRequest request, Model model, @RequestParam HashMap<String, Object> map) throws Exception {
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		resultMap = onedayService.onedayUpdate(map);
		return new Gson().toJson(resultMap);
	}
    
   
	
} 