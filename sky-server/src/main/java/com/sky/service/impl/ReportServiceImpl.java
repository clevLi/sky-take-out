package com.sky.service.impl;

import com.google.j2objc.annotations.AutoreleasePool;
import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.result.Result;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

@Service
@Slf4j

public class ReportServiceImpl implements ReportService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private WorkspaceService workspaceService;

    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
       //当前集合用于存放从begin到end范围内的日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<Double> turnoverList = new ArrayList<>();
        for (LocalDate date: dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status", Orders.COMPLETED);

            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover==null?0.0:turnover;
            turnoverList.add(turnover);
        }
        String date = StringUtils.join(dateList, ",");
        String turnover = StringUtils.join(turnoverList, ",");
        TurnoverReportVO turnoverReportVO = TurnoverReportVO.builder()
                                            .dateList(date)
                                            .turnoverList(turnover)
                                            .build();
        return turnoverReportVO;
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> newUserList = new ArrayList();
        List<Integer> totalUserList = new ArrayList();
        for (LocalDate date: dateList){
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("end",endTime);
            Integer totalUser = userMapper.countByMap(map);
            totalUserList.add(totalUser);
            map.put("begin",beginTime);
            Integer newUser = userMapper.countByMap(map);
            newUserList.add(newUser);

        }
        UserReportVO userReportVO = UserReportVO.builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(totalUserList, ",")).build();

        return  userReportVO;
    }

    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Integer> orderCountList = new ArrayList();
        List<Integer> validOrderCountList = new ArrayList();

        // 遍历集合查询每天订单总数，有效订单数
        for (LocalDate date: dateList) {
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map = new HashMap();
            map.put("begin",beginTime);
            map.put("end",endTime);
            Integer order = orderMapper.countByMap(map);
            orderCountList.add(order);
            map.put("status",Orders.COMPLETED);
            Integer validOrder = orderMapper.countByMap(map);
            validOrderCountList.add(validOrder);
        }
        Integer totalOrders = orderCountList.stream().reduce(Integer::sum).get();
        Integer validOrders = validOrderCountList.stream().reduce(Integer::sum).get();
        Double orderCompletionRate = totalOrders==0?0.0:validOrders.doubleValue()/totalOrders;
        OrderReportVO orderReportVO = OrderReportVO.builder().dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .orderCompletionRate(orderCompletionRate)
                .totalOrderCount(totalOrders)
                .validOrderCount(validOrders)
                .validOrderCountList(StringUtils.join(validOrderCountList,","))
                .build();
        return orderReportVO;
    }

    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        //当前集合用于存放从begin到end范围内的日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while(!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }
        List<String> nameList = new ArrayList();
        List<Integer> numberList = new ArrayList();

        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> goodsSales = orderDetailMapper.getSalesTop10(beginTime,endTime);
        for(GoodsSalesDTO goods : goodsSales){
            nameList.add(goods.getName());
            numberList.add(goods.getNumber());
        }

        return SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList,","))
                .numberList(StringUtils.join(numberList,","))
                .build();
    }

    @Override
    public void exportBusinessData(HttpServletResponse httpResponse) {
        //1 查询数据库，获取营业数据
        LocalDate end = LocalDate.now().minusDays(1);
        LocalDate begin = LocalDate.now().minusDays(30);
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        BusinessDataVO businessData = workspaceService.getBusinessData(beginTime, endTime);
        //2 通过POI写入excel文件
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template\\report.xlsx");
        try {
            XSSFWorkbook excel = new XSSFWorkbook(in);
            XSSFSheet sheet = excel.getSheetAt(0);
            //填充时间
            sheet.getRow(1).getCell(1).setCellValue("日期:从"+begin+"到"+end);
            //填充第4行
            XSSFRow row3 = sheet.getRow(3);
            row3.getCell(2).setCellValue(businessData.getTurnover());
            row3.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row3.getCell(6).setCellValue(businessData.getNewUsers());

            //填充第5行
            XSSFRow row4 = sheet.getRow(4);
            row4.getCell(2).setCellValue(businessData.getValidOrderCount());
            row4.getCell(4).setCellValue(businessData.getUnitPrice());
            //填充明细数据
            for (int i = 0; i < 30; i++) {
                LocalDate date = begin.plusDays(i);
                BusinessDataVO data = workspaceService.getBusinessData(LocalDateTime.of(date, LocalTime.MIN), LocalDateTime.of(date, LocalTime.MAX));
                XSSFRow rowi = sheet.getRow(i + 7);
                rowi.getCell(1).setCellValue(date.toString());
                rowi.getCell(2).setCellValue(data.getTurnover());
                rowi.getCell(3).setCellValue(data.getValidOrderCount());
                rowi.getCell(4).setCellValue(data.getOrderCompletionRate());
                rowi.getCell(5).setCellValue(data.getUnitPrice());
                rowi.getCell(6).setCellValue(data.getNewUsers());
            }
            //3 通过输出流下载到客户端浏览器

            ServletOutputStream out = httpResponse.getOutputStream();
            excel.write(out);

            excel.close();
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }



    }

}
