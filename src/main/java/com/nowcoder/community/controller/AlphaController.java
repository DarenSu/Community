package com.nowcoder.community.controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;


@RestController      //进行模块的注明，此处为控制模块
@RequestMapping("/alpha")
public class AlphaController {

    // 在SpringMVC中怎么获取请求对象和响应对象，请求对象里面封装请求对象，响应对象里面封装响应对象
    @RequestMapping("/http")
    public void http(HttpServletRequest request, HttpServletResponse response){
        // 获取请求数据
        System.out.println(request.getMethod());
        System.out.println(request.getServletPath());
        Enumeration<String> enumeration = request.getHeaderNames();// 迭代器
        while (enumeration.hasMoreElements()){
            String name= enumeration.nextElement();
            String value= request.getHeader(name);
            System.out.println(name+ ": "+ value);
        }
        System.out.println(request.getParameter("code"));

        // 返回响应数据
        response.setContentType("text/html;charset=utf-8");
        try ( PrintWriter writer= response.getWriter();){
           writer.write("<h1>牛客网<h1>");
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    // GET请求

    // /students?current=1&limit=20
    @RequestMapping(path = "/students", method = RequestMethod.GET)
    @ResponseBody
    public String getStudents(
            @RequestParam(name = "current", required = false, defaultValue = "1") int current,
            @RequestParam(name = "limit", required = false, defaultValue = "10")int limit) {
        System.out.println(current);
        System.out.println(limit);
        return "some students";
    }

    // 不查询所有学生，只查询一个学生
    // /student/123
    @RequestMapping(path = "/student/{id}", method = RequestMethod.GET)
    @ResponseBody
    public String getStudent(@PathVariable("id") int id){
        System.out.println(id);
        return "a student!";
    }

    // 浏览器先向服务器提交数据
    // POST请求
    @RequestMapping(path = "/student", method = RequestMethod.POST)
    @ResponseBody
    public String saveStudent(String name, int age){
        System.out.println(name);
        System.out.println(age);
        return "success!";
    }

    // 响应HTML数据

    @RequestMapping(path = "/teacher", method = RequestMethod.GET)
//    @ResponseBody   // 因为返回的是HTML数据，所以不用加这个注解，返回的就是
    //ModelAndView  返回两个model和view,然后由模板引擎进行渲染
    public ModelAndView getTeacher(){
        ModelAndView mav= new ModelAndView();
        mav.addObject("name", "张三");
        mav.addObject("age", 30);
        // 设置模板的路径个名字   - 一会将模板放在templates下，所以templates不用写，
        // 并且模板是HTML，后缀名是固定的不用写，所以写出文件的名字就可以
        mav.setViewName("/demo/view");
        return  mav;
    }

    // 另一种HTML数据响应的方式
    @RequestMapping(path = "/school", method = RequestMethod.GET)
//    @ResponseBody   // 因为返回的是HTML数据，所以不用加这个注解，返回的就是
    //返回的String指的是返回的view路径
    public String getSchool(Model model){
        model.addAttribute("name","北京大学");
        model.addAttribute("age",80);
        return  "/demo/view";
    }


    // 响应json数据    通常在异步请求中这样响应
    // 当前代码时时刻刻都能得到java对象   后台将java对象给浏览器，浏览器用JS解析，这时候使用JSON比较好解析
    // Java对象 -> JSON对象 -> JS对象
    @RequestMapping(path = "/json", method = RequestMethod.GET)
    @ResponseBody   // 不加这个默认返回HTML， 加上的话返回json
    public Map<String, Object> getJson(){
        Map<String, Object> map= new HashMap<>();
        map.put("name", "张三");
        map.put("age",23);
        map.put("salary", 80000);
        System.out.println(map);
        return map;
    }
    // 查询所有的员工
    @RequestMapping(path = "/jsons", method = RequestMethod.GET)
    @ResponseBody   // 不加这个默认返回HTML， 加上的话返回json
    public List<Map<String, Object>> getJsons(){
        List<Map<String, Object>> list= new ArrayList<>();
        Map<String, Object> map= new HashMap<>();
        map.put("name", "张三");
        map.put("age",23);
        map.put("salary", 80000);
//        System.out.println(map);
        list.add(map);

        map= new HashMap<>();
        map.put("name", "张三收到");
        map.put("age",23);
        map.put("salary", 80000);
        list.add(map);

        map= new HashMap<>();
        map.put("name", "张按时三");
        map.put("age",23);
        map.put("salary", 80000);
        list.add(map);

        map= new HashMap<>();
        map.put("name", "张请问三");
        map.put("age",23);
        map.put("salary", 80000);
        list.add(map);

        return list;
    }

}


