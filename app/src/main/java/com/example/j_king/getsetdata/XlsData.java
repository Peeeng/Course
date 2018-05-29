package com.example.j_king.getsetdata;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

/**
 * 获取xls文件数据
 */
class XlsData {
    private Workbook workBook;
    private Sheet workSheet;
    private Integer rowCount ;
    private Integer columnCount ;

    XlsData(InputStream in){
        try {
            workBook = Workbook.getWorkbook(in) ;
            workSheet = workBook.getSheet(0);

            rowCount = workSheet.getRows();
            columnCount = workSheet.getColumns();

        } catch (IOException | BiffException e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param columnIndex 表格的列标
     * @param rowIndex 表格的行标
     * @return 返回表格的（列标，行标）位置的数据
     */
    private List<Map<String,Object>> getOneData(int columnIndex,int rowIndex){
        String data =  workSheet.getCell(columnIndex,rowIndex).getContents();//以String类型输入
        if(data.equals(" ") || data.isEmpty())
            return null ;

        List<Map<String,Object> > params = new ArrayList<>();
        String []xlsData = data.split("\n");
        try {

            for(int i = 1 ; i <= xlsData.length/5 ; i++){
                Map<String,Object> param = new HashMap<String,Object>() ;
                int j = 5*(i-1) ;
                String className = xlsData[1+j] ;
                String teacherName = (xlsData[2+j].split("\\("))[0];
                String classWeeks = (xlsData[3+j].split("\\["))[0];
                String classAddr = xlsData[4+j];
                param.put(CourseDB.cName,className) ;
                param.put(CourseDB.cTeacher,teacherName) ;
                param.put(CourseDB.cWeeks,splitClassWeeks(classWeeks)) ;
                //将星期数存入Map，周一到周日对应 1 到 7
                param.put(CourseDB.cWeekday,columnIndex );
                //将节次信息存入Map，节次为：1,3,5,7,9，11
                param.put(CourseDB.cTime,(rowIndex-2)*2 - 1) ;
                param.put(CourseDB.cAddr,classAddr) ;
                params.add(param);
            }

        }catch (Exception e){
            e.printStackTrace();
            return params ;
        }
        return params ;
    }

    /**
     * 获取每个单元格的数据
     * @return  单元格数据列表
     */
    ArrayList<Map<String,Object>> getAllData(){
        ArrayList<Map<String,Object>> courseList = new ArrayList<>() ;
        List<Map<String,Object>> tmpList  ;

        for(int i = 1 ; i < columnCount ; i++){
            for(int j = 3 ; j < rowCount ; j++){
                tmpList = getOneData(i,j) ;
                if( tmpList == null || tmpList.isEmpty()) {
                    continue ;
                }
                for(int k = 0 ; k < tmpList.size() ; k++){
                    Map<String, Object> tmpitem = tmpList.get(k);

                    int [] weeks = (int[]) tmpitem.get(CourseDB.cWeeks);
                    for(Integer week :weeks){
                        Map<String,Object> newtmpitem = new HashMap<>() ;
                        newtmpitem.putAll(tmpitem);
                        newtmpitem.put(CourseDB.cWeeks, week);
                        courseList.add(newtmpitem);
                    }
                }
            }
        }
        return courseList ;
    }

    /**
     *
     * @param classWeeks 教学周次字符串，如（4-17）
     * @return  分割开字符串后产生教学周次的整形数组
     */
    private int[] splitClassWeeks(String classWeeks){
        int count = 0 ;
        String[] weekBeginEnd = classWeeks.split(",");
        int [] weeks = new int[25];
        for (String aWeekBeginEnd : weekBeginEnd) {
            if (aWeekBeginEnd.contains("-")) {
                String[] weekBE = aWeekBeginEnd.split("-");
                int begin = Integer.parseInt(weekBE[0]);
                int end = Integer.parseInt(weekBE[1]);

                for (int j = begin; j <= end; j++) {
                    weeks[count++] = j;
                }
            } else
                weeks[count++] = Integer.valueOf(aWeekBeginEnd);
        }
        return  Arrays.copyOf(weeks, count);
    }

    /**
     * 关闭数据的连接
     */
    void closeConnect(){
        workBook.close();
    }
}
