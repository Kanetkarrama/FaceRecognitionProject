/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectdatabase;

import static com.oracle.jrockit.jfr.ContentType.Bytes;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import static java.sql.JDBCType.BLOB;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import static java.sql.Types.BLOB;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import projectreport.visitByCategory;

/**
 *
 * @author liuziqi
 */
public class StudentDB {

    private String url = "jdbc:derby:StudentDB";
    private String username = "student";
    private String password = "cmu";
    private Connection conn;
    private Statement stmt;
    private ResultSet rs;
    ArrayList<String> BasicInfo;
    String pattern = "yyyy/MM/dd";
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);

    /**
     *
     */
    public StudentDB() {
        try {
            conn = DriverManager.getConnection(url, username, password);
            stmt = conn.createStatement();
            System.out.println("Done");

        } catch (SQLException se) {
            System.out.println("Error: " + se);
        }
    }

    /**
     *
     * @param andrewID
     * @return
     */
    public ArrayList<String> getBasicInfo(String andrewID) {
        ArrayList<String> basicInfo = new ArrayList<>();

        String query1 = "SELECT * FROM Student.BASICINFO";

        try {
            rs = stmt.executeQuery(query1);
            while (rs.next()) {
                if (rs.getString("ANDREWID").equals(andrewID)) {
                    basicInfo.add(rs.getString("NAME"));
                    basicInfo.add(rs.getString("EMAIL"));
                    basicInfo.add(rs.getString("ADDRESS"));
                    basicInfo.add(rs.getString("PHONE"));
                    basicInfo.add(rs.getString("GENDER"));
                    basicInfo.add(rs.getString("PROGRAM"));
                    basicInfo.add(rs.getString("CONCENTRATION"));
                    basicInfo.add(rs.getString("SEMESTER"));
                    basicInfo.add(rs.getString("SUPERVISOR"));
                    break;
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(StudentDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return basicInfo;
    }

    /**
     *
     * @param andrewID
     * @return
     */
    public int totalVisitCounts(String andrewID) {
        String query2 = "SELECT * FROM STUDENT.VISITS";
        int count = 0;
        try {

            rs = stmt.executeQuery(query2);
            while (rs.next()) {
                if (rs.getString("ANDREWID").equals(andrewID)) {
                    count++;
                }

            }

        } catch (SQLException ex) {
            Logger.getLogger(StudentDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return count;
    }

    /**
     *
     * @param andrewID
     * @return
     */
    public ArrayList<String> getLastVisitReason(String andrewID) {
        ArrayList<String> lastVisit = new ArrayList<>();

        String query3 = "SELECT * FROM STUDENT.VISITS";
        String lastReasonID = "";
        Date lastDate;

        int compareDate;
        Calendar myCalendar = new GregorianCalendar(2000, 1, 1);
        Date defaultDate = myCalendar.getTime();
        lastDate = defaultDate;
        String dateFormat = "dd/MM/yyyy";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);

        try {
            Statement stmt2 = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            ResultSet rs2 = stmt2.executeQuery(query3);
            //search for the latest visit
            rs2.last();
            while (rs2.previous()) {
                if (rs2.getString("ANDREWID").equals(andrewID)) {
                    compareDate = rs2.getDate("VISITDATE").compareTo(lastDate);
                    if (compareDate > 0) {
                        lastDate = rs2.getDate("VISITDATE");
                        lastReasonID = rs2.getString("REASONID");
                    }
                }
            }

            //Search Reason ID           
            String lastReason = getReasonDescription(lastReasonID);
            lastVisit.add(lastReason);
            String formatedDate = sdf.format(lastDate);
            lastVisit.add(formatedDate);
        } catch (SQLException ex) {
            Logger.getLogger(StudentDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lastVisit;
    }

    /**
     *
     * @param reasonID
     * @return
     */
    public String getReasonDescription(String reasonID) {
        String query4 = "SELECT * FROM STUDENT.VISITREASON";
        String reasonDescription = null;
        try {
            rs = stmt.executeQuery(query4);
            while (rs.next()) {
                if (rs.getString("REASONID").equals(reasonID)) {
                    reasonDescription = rs.getString("DESCRIPTION");
                    break;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(StudentDB.class.getName()).log(Level.SEVERE, null, ex);
        }
        return reasonDescription;
    }

    /**
     *
     * @param program
     * @return
     */
    public String getAnnouncement(String program) {
        String query5 = "SELECT * FROM STUDENT.ANNOUNCEMENT";
        String announcement = null;

        try {
            rs = stmt.executeQuery(query5);
            while (rs.next()) {
                if (rs.getString("PROGRAM").equals(program)) {
                    announcement = rs.getString("ANNOUNCEMENT");
                    break;
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(StudentDB.class.getName()).log(Level.SEVERE, null, ex);
        }

        return announcement;
    }

    /**
     *
     * @param startDate
     * @param endDate
     * @return
     * @throws SQLException
     */
    public int[] countVisitGender(Date startDate, Date endDate) throws SQLException {
        int[] count = new int[2];

        String query3
                = "SELECT BASICINFO.GENDER, COUNT(BASICINFO.ANDREWID) "
                + "FROM BASICINFO "
                + "JOIN VISITS "
                + "ON VISITS.ANDREWID=BASICINFO.ANDREWID "
                + "WHERE VISITS.VISITDATE BETWEEN ? AND ? "
                + "GROUP BY BASICINFO.GENDER";

        PreparedStatement preparedStatement = conn.prepareStatement(query3);

        java.sql.Date fromDate = new java.sql.Date(startDate.getTime());
        java.sql.Date toDate = new java.sql.Date(endDate.getTime());

        preparedStatement.setDate(1, fromDate);
        preparedStatement.setDate(2, toDate);
        rs = preparedStatement.executeQuery();
        String columnName;
        int femaleCount = 0;
        int maleCount = 0;

        while (rs.next()) {
            columnName = rs.getString("GENDER");
            if ("Female".equals(columnName)) {
                femaleCount = rs.getInt(2);
            } else {
                maleCount = rs.getInt(2);
            }

        }
        count[0] = femaleCount;
        count[1] = maleCount;

        return count;

    }

    /**
     *
     * @param startDate
     * @param endDate
     * @return
     * @throws SQLException
     */
    public ArrayList<visitByCategory> VisitsByCategory(Date startDate, Date endDate) throws SQLException {
        String queryString = "SELECT VISITREASON.DESCRIPTION, BASICINFO.\"NAME\", VISITS.VISITDATE "
                + "FROM BASICINFO "
                + "JOIN VISITS "
                + "ON VISITS.ANDREWID = BASICINFO.ANDREWID "
                + "JOIN VISITREASON "
                + "ON VISITS.REASONID= VISITREASON.REASONID "
                + "WHERE VISITS.VISITDATE BETWEEN ? AND ? ";
        //+
        //"ORDER BY 1";

        ArrayList<visitByCategory> count = new ArrayList<>();
        PreparedStatement preparedStatement = conn.prepareStatement(queryString);

        java.sql.Date fromDate = new java.sql.Date(startDate.getTime());
        java.sql.Date toDate = new java.sql.Date(endDate.getTime());

        preparedStatement.setDate(1, fromDate);
        preparedStatement.setDate(2, toDate);
        rs = preparedStatement.executeQuery();

        String description;
        String name;
        String visitDate;
        Date date;
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        while (rs.next()) {
            description = rs.getString(1);
            name = rs.getString(2);
            date = rs.getDate(3);
            visitDate = df.format(date);
            System.out.println(visitDate);
            visitByCategory vbc = new visitByCategory(description, name, visitDate);
            count.add(vbc);
            //System.out.println(vbc);
        }

        return count;
    }

    /**
     *
     * @param andrewID
     * @param name
     * @param email
     * @param address
     * @param phone
     * @param gender
     * @param program
     * @param concentration
     * @param semester
     * @param supervisor
     * @throws SQLException
     */
    public void insertNewStudent(String andrewID, String name, String email,
            String address, String phone, String gender, String program, String concentration, String semester,
            String supervisor) throws SQLException {

        String queryString = "insert into STUDENT.BASICINFO (ANDREWID, NAME, EMAIL, "
                + "ADDRESS, PHONE, GENDER, PROGRAM, CONCENTRATION, SEMESTER, SUPERVISOR) "
                + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = conn.prepareStatement(queryString);

        preparedStatement.setString(1, andrewID);
        preparedStatement.setString(2, name);
        preparedStatement.setString(3, email);
        preparedStatement.setString(4, address);
        preparedStatement.setString(5, phone);
        preparedStatement.setString(6, gender);
        preparedStatement.setString(7, program);
        preparedStatement.setString(8, concentration);
        preparedStatement.setString(9, semester);
        preparedStatement.setString(10, supervisor);

        preparedStatement.executeUpdate();
        System.out.println("Insert Done");
        
    }

    /**
     *
     * @param andrewID
     * @param reasonID
     * @param date
     * @throws SQLException
     */
    public void insertNewVisit(String andrewID, String reasonID, Date date) throws SQLException {

        String queryString = "INSERT INTO STUDENT.VISITS\n" +
                             "SELECT ?,VISITREASON.REASONID, ?\n" +
                             "FROM STUDENT.VISITREASON\n" +
                             "WHERE VISITREASON.DESCRIPTION = ?";
        PreparedStatement preparedStatement = conn.prepareStatement(queryString);
        preparedStatement.setString(1, andrewID);
        preparedStatement.setString(3, reasonID);
        preparedStatement.setDate(2, new java.sql.Date(date.getTime()));
        preparedStatement.executeUpdate();
        System.out.println("Insert Done");
        
    }

    /**
     *
     * @return @throws SQLException
     */
    public ArrayList<String> getReasonDescription() throws SQLException {
        ArrayList<String> reasonList = new ArrayList<>();
        String queryString = "SELECT * FROM STUDENT.VISITREASON";
        rs = stmt.executeQuery(queryString);

        while (rs.next()) {
            reasonList.add(rs.getString("DESCRIPTION"));
        }
        
        return reasonList;
    }

    public ArrayList<String> getAndrewIDlist() throws SQLException {
        ArrayList<String> andrewIDlist = new ArrayList<>();
        String queryStirng = "SELECT DISTINCT ANDREWID FROM Student.BASICINFO";
        rs = stmt.executeQuery(queryStirng);
        while (rs.next()) {
            andrewIDlist.add(rs.getString(1));
        }
        
        return andrewIDlist;
    }

    public ArrayList<String> getReasonList() throws SQLException{
        ArrayList<String> reasonList = new ArrayList<>();
        String queryStirng = "SELECT DISTINCT DESCRIPTION FROM STUDENT.VISITREASON";
        rs = stmt.executeQuery(queryStirng);
        while (rs.next()) {
            reasonList.add(rs.getString(1));
        }
        
        
        return reasonList;
        
    }
        //return isMatch;
        public int verifyLogin (String loginName, String loginPassword) throws SQLException
        {
            int access = 0;
            try 
            {
            String adminQuery = "SELECT * FROM USERTABLE WHERE USERTABLE.USERNAME = ? AND USERTABLE.PASSWORD = ?";
            PreparedStatement preparedStatement = conn.prepareStatement(adminQuery);
            preparedStatement.setString(1, loginName);
            preparedStatement.setString(2, loginPassword);
            rs = preparedStatement.executeQuery();
                if (rs.next())
                {
                    access = rs.getInt(3);
                }
            }
            catch (SQLException e)
            {
                System.out.println(e.getMessage());
            }
            
            return access;
        }
        public boolean insertNewStudent2(String andrewID, String name, String email,
            String address, String phone, String gender, String program, String concentration, String semester,
            String supervisor) throws SQLException {
        
        boolean isSuccessful = false;

        String query = "SELECT * FROM BASICINFO where ANDREWID = ?";
        
        PreparedStatement preparedStatement1 = conn.prepareStatement(query);
        preparedStatement1.setString(1, andrewID);
        rs = preparedStatement1.executeQuery();

        if (rs.next()) {

        }else{            
            String queryString = "insert into STUDENT.BASICINFO (ANDREWID, NAME, EMAIL, "
                    + "ADDRESS, PHONE, GENDER, PROGRAM, CONCENTRATION, SEMESTER, SUPERVISOR) "
                    + "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement preparedStatement = conn.prepareStatement(queryString);

            preparedStatement.setString(1, andrewID);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, address);
            preparedStatement.setString(5, phone);
            preparedStatement.setString(6, gender);
            preparedStatement.setString(7, program);
            preparedStatement.setString(8, concentration);
            preparedStatement.setString(9, semester);
            preparedStatement.setString(10, supervisor);
            preparedStatement.executeUpdate();
            isSuccessful = true;
            
            
        
        }        
        return isSuccessful;
    }
        
        public void close() throws SQLException{
        rs.close();
        stmt.close();
        conn.close();
        }
}
