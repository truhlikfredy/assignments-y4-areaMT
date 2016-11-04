//package antonkrug.eu.test;
//
//import static org.junit.Assert.*;
//
//import java.util.Date;
//
//import org.junit.BeforeClass;
//import org.junit.Test;
//
//import antonkrug.eu.DataAccess;
//import antonkrug.eu.DataAccessJdbc;
//import antonkrug.eu.Employee;
//import antonkrug.eu.Pair;
//
///**
// * These test will start from scratch with empty database and test the DAO
// * methods. Use with CAUTION!
// * 
// * @author  Anton Krug
// * @date    27.9.2016
// * @version 1
// *
// */
//public class DataAccessJdbcTest {
//  
//  //How many employees to generate. It needs to be at least 10 for tests to work well.
//  //Too big value could slow down the computer.
//  private static final Integer GENERATE_EMPLOYEES = 50; 
//
//  private static DataAccess dao;
//
//
//  /**
//   * CAUTION! This will remove the existing database and create empty one
//   */
//  private static void wipeDb() {
//    @SuppressWarnings("unused")
//    Pair<Boolean, String> ret = dao.executeQuery(
//      "DROP DATABASE IF EXISTS `Database1`;");
//    //    System.out.println(ret);
//  }
//  
//  /**
//   * Create new schema from scratch
//   */
//  @SuppressWarnings("unused")
//  private static void createDb() {
//    
//    Pair<Boolean, String> retDb = dao.executeQuery(
//      "CREATE DATABASE IF NOT EXISTS `Database1`;");
//    //System.out.println(retDb);
//    
//    Pair<Boolean, String> retUse = dao.executeQuery(
//      "USE `Database1`;");
//    //System.out.println(retUse);
//    
//    Pair<Boolean, String> retDepartment = dao.executeQuery(
//      "CREATE TABLE IF NOT EXISTS `Department` (" +
//        "`Name` varchar(100) NOT NULL," +
//        "`Number` int(11) NOT NULL DEFAULT '0'," +
//        "`Locations` varchar(100) DEFAULT NULL," +
//        "PRIMARY KEY (`Number`)" +
//      ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
//    //    System.out.println(retDepartment);
//    
//    Pair<Boolean, String> retProject = dao.executeQuery(
//      "CREATE TABLE IF NOT EXISTS `Project` (" +
//        "`Name` varchar(80) NOT NULL," +
//        "`Number` int(11) NOT NULL," +
//        "`Location` varchar(80) DEFAULT NULL," +
//        "`Controlled_By` int(11) DEFAULT NULL," +
//        "PRIMARY KEY (`Name`,`Number`)," +
//        "KEY `FK_Project_Department` (`Controlled_By`)" +
//      ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
//    //System.out.println(retProject);
//    
//    Pair<Boolean, String> retEmployee = dao.executeQuery(
//      "CREATE TABLE IF NOT EXISTS `Employee` (" +
//        "`Ssn` int(11) NOT NULL AUTO_INCREMENT," +
//        "`Bdate` date DEFAULT NULL," +
//        "`Name` varchar(80) DEFAULT NULL," +
//        "`Address` varchar(160) DEFAULT NULL," +
//        "`Salary` decimal(7,0) DEFAULT NULL," +
//        "`Sex` char(1) DEFAULT NULL," +
//        "`Works_For` int(11) NOT NULL," +
//        "`Manages` int(11) DEFAULT NULL," +
//        "`Supervises` int(11) DEFAULT NULL," +
//        "PRIMARY KEY (`Ssn`)," +
//        "KEY `Work_For` (`Works_For`)" +
//      ") ENGINE=InnoDB DEFAULT CHARSET=latin1;");
//    //System.out.println(retEmployee);
//  }
//
//
//  private static void connectDb() {
//    dao = new DataAccessJdbc();
//    Pair<Boolean,String> ret = dao.connect();
//    
//    //make sure we are connected without issues
//    if (!ret.getFirst()) System.err.println(ret.getSecond());
//    assertTrue(ret.getSecond(), ret.getFirst());
//  }
//
//  
//  /**
//   * Will populate database with GENERATE_EMPLOYEES+2 employees
//   */
//  private static void addEmployees() {   
//    //System.out.println("Populating database with " + (GENERATE_EMPLOYEES+2) + " employees, please wait.");
//    
//    dao.addEmployee(new Employee(-1, "Anton Krug", "Waterford", 100, "M", new Date(384134400000L)));
//    
//    for (int i=0; i<GENERATE_EMPLOYEES; i++) {
//      dao.addEmployeeRandom();
//    }
//    
//    dao.addEmployee(new Employee(-1, "Shane Lacey", "Tramore", 120, "M", new Date(886550400000L)));
//  }
//  
//
//  /**
//   * Warning this will wipe the database and start clean one from scratch, be
//   * careful.
//   * 
//   * @throws Exception
//   */
//  @BeforeClass
//  public static void setUp() throws Exception {
//    connectDb();
//    wipeDb();
//    createDb();
//    addEmployees();
//  }
//  
//
//  @Test
//  public void testAdding() {
//    dao.getEmployees();
//    
//    assertEquals("Size of the Employees table", GENERATE_EMPLOYEES+2, dao.getSize());
//  }
//  
//  
//  /**
//   * First and last employee should have values exactly as I
//   * added them
//   */
//  @Test
//  public void testFirstAndLastWithGetters() {
//    dao.getEmployees();
//    
//    dao.firstEmployee();
//    Employee current = dao.getCurrentEmployee();    
//    assertEquals("Anton Krug",current.getName()     );
//    assertEquals("Waterford", current.getAddress()  );
//    assertEquals(100,         current.getSalary()   );
//    assertEquals("M",         current.getSex()      );
//    assertEquals("05/3/1982", current.getDobString());
//    
//    dao.lastEmployee();
//    current = dao.getCurrentEmployee();    
//    assertEquals("Shane Lacey",current.getName()     );
//    assertEquals("Tramore",    current.getAddress()  );
//    assertEquals(120,          current.getSalary()   );
//    assertEquals("M",          current.getSex()      );
//    assertEquals("04/2/1998",  current.getDobString());
//  }
//
//  
//  /**
//   * Second employee shouldn't match first
//   * and second to last employee shoudn't match last one
//   */
//  @Test
//  public void testNextAndPreviousWithGetters() {
//    dao.getEmployees();
//
//    //second employee
//    dao.firstEmployee();
//    dao.nextEmployee();
//    Employee current = dao.getCurrentEmployee();    
//    assertNotEquals("Anton Krug",current.getName()     );
//    assertNotEquals("Waterford", current.getAddress()  );
//    
//    dao.lastEmployee();
//    dao.previousEmployee();
//    current = dao.getCurrentEmployee();    
//    assertNotEquals("Shane Lacey",current.getName()     );
//    assertNotEquals("Tramore",    current.getAddress()  );
//  }
//  
//  
//  @Test 
//  public void testDelete() {
//    dao.getEmployees();
//
//    //delete second employee
//    dao.firstEmployee();
//    dao.nextEmployee();
//    dao.removeEmployee(dao.getCurrentEmployee().getSsn());
//    dao.getEmployees();
//    assertEquals((GENERATE_EMPLOYEES+2) -1,dao.getSize());
//    
//    //delete second to last employee
//    dao.lastEmployee();
//    dao.previousEmployee();
//    dao.removeEmployee(dao.getCurrentEmployee().getSsn());
//    dao.getEmployees();
//    assertEquals((GENERATE_EMPLOYEES+2) -2,dao.getSize());
//       
//    //delete anton employee and make sure now first employee is not anton
//    dao.firstEmployee();
//    dao.removeEmployee(dao.getCurrentEmployee().getSsn());
//    dao.getEmployees();
//    assertEquals((GENERATE_EMPLOYEES+2) -3,dao.getSize());
//    Employee current = dao.getCurrentEmployee();    
//    assertNotEquals("Anton Krug",current.getName()     );
//    assertNotEquals("Waterford", current.getAddress()  );
//    
//    //delete shane employee and make sure now last employee is not shane
//    dao.firstEmployee();
//    dao.removeEmployee(dao.getCurrentEmployee().getSsn());
//    dao.getEmployees();
//    assertEquals((GENERATE_EMPLOYEES+2) -4,dao.getSize());
//    current = dao.getCurrentEmployee();    
//    assertNotEquals("Shane Lacey",current.getName()     );
//    assertNotEquals("Tramore",    current.getAddress()  );
//  }
//  
//  
//}
