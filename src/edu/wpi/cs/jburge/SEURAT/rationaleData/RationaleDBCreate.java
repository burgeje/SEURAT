package edu.wpi.cs.jburge.SEURAT.rationaleData;

import java.sql.SQLSyntaxErrorException;

/**
 * This class contains the statements that are used in the creation of a rationale DB
 * and methods that retrieve and format those statements.
 *
 */
public class RationaleDBCreate
{	
	/**
	 * Formats part of an SQL table statement.
	 * @param pQuery the partial statement
	 * @return pQuery, properly formatted
	 */
	private static String tablePart(String pQuery)
	{
		return pQuery + ",";
	}
	
	/**
	 * Formats the beginning of a create table statement.
	 * @param pName the table name
	 * @return pName, properly formatted
	 */
	private static String beginTable(String pName)
	{
		return "CREATE TABLE " + RationaleDBUtil.escapeTableName(pName) + " (";
	}
	
	/**
	 * Formats the end of a create table statement.
	 * @param pQuery the partial statement
	 * @return pQuery, with the end properly formatted
	 */
	private static String endTable(String pQuery)
	{
		return pQuery + ")";
	}
	
	/**
	 * Returns the ontentries insert statement.
	 * @return String containing the statement
	 */
	private static String ontEntryInsert(String pString)
	{
		// Remove the first argument from the ontEntryInsert (done to avoid
		// having to manually change them all with a change that was made to the schema)
		String newPString = pString.substring(pString.indexOf(",") + 1);
		return "INSERT INTO ontentries (name, description, importance) " +
			"values (" + newPString + ")";
	}
	
	/**
	 * Returns the ontrelationships insert statement.
	 * @return String containing the statement
	 */
	private static String ontRelationshipInsert(String pString)
	{
		return "INSERT INTO ontrelationships values (" + pString + ")";
	}
	
	/**
	 * Returns the appropriate string value for AUTO_INCREMENT functionality
	 * if the current database type supports it.
	 * @return String containing the string value
	 */
	private static String autoIncrement() {
		if( RationaleDBUtil.checkDBType() == RationaleDBUtil.DBTypes.MYSQL )
			return "AUTO_INCREMENT";
		
		return "GENERATED ALWAYS AS IDENTITY";
	}
	
	/**
	 * String array containing values to be inserted into ontEntries.
	 */
	
	private static String INSERT_ONTENTRY[] =
	{
		ontEntryInsert("1,'Argument-Ontology','','Moderate'"),
		ontEntryInsert("2,'Affordability Criteria','These arguments refer to the cost of the software','Moderate'"),
		ontEntryInsert("3,'Development Cost','Development cost is a big factor in affordability','Moderate'"),
		ontEntryInsert("4,'Uses Standard Tools and Environments','Using standard tools and environments is a good way to keep costs down','Moderate'"),
		ontEntryInsert("5,'{is a| uses a} standard development tool(s)','','Moderate'"),
		ontEntryInsert("6,'{is a | uses a} standard language','','Moderate'"),
		ontEntryInsert("7,'Uses Familiar Tools and Environments','If the developers are familiar, it is cheaper to use','Moderate'"),
		ontEntryInsert("8,'{is a | uses a} familiar language','','Moderate'"),
		ontEntryInsert("9,'utilizes developer experience','performance is better if the developer is experienced at what they do','Moderate'"),
		ontEntryInsert("10,'{is a | uses a} familiar development environment','','Moderate'"),
		ontEntryInsert("11,'{is a | uses a} familiar hardware platform','','Moderate'"),
		ontEntryInsert("12,'Reduces Development Time','A good thing!','Moderate'"),
		ontEntryInsert("13,'is component based','','Moderate'"),
		ontEntryInsert("14,'uses COTS/GOTS software','','Moderate'"),
		ontEntryInsert("15,'reduces customization','','Moderate'"),
		ontEntryInsert("16,'utilizes existing code developed in-house','','Moderate'"),
		ontEntryInsert("17,'uses automatically generated code','','Moderate'"),
		ontEntryInsert("18,'Reduces Project Sucess Risk','Are we in danger of not completing?','Moderate'"),
		ontEntryInsert("19,'{is a | uses a} mature language','','Moderate'"),
		ontEntryInsert("20,'{is a | uses a} mature process','','Moderate'"),
		ontEntryInsert("21,'Reduces Prototyping Cost','Another place to look at cost','Moderate'"),
		ontEntryInsert("22,'reduces prototyping time','','Moderate'"),
		ontEntryInsert("23,'Reduces Risk Analysis Cost','','Moderate'"),
		ontEntryInsert("24,'reduces risk analysis time','','Moderate'"),
		ontEntryInsert("25,'Reduces Component Integration Cost','Integration can be a cost savings or a bust','Moderate'"),
		ontEntryInsert("26,'reduces component integration time','','Moderate'"),
		ontEntryInsert("27,'Reduces Domain Analysis Cost','','Moderate'"),
		ontEntryInsert("28,'reduces domain analysis time','','Moderate'"),
		ontEntryInsert("29,'Reduces Inspection Cost','','Moderate'"),
		ontEntryInsert("30,'reduces inspection time','','Moderate'"),
		ontEntryInsert("31,'Deployment Cost','How much does it cost to deploy the system?','Moderate'"),
		ontEntryInsert("32,'Minimizes Equipment Cost','','Moderate'"),
		ontEntryInsert("33,'reduces hardware cost','','Moderate'"),
		ontEntryInsert("34,'Minimizes External Software Cost','','Moderate'"),
		ontEntryInsert("35,'{is | uses} open source','','Moderate'"),
		ontEntryInsert("36,'Minimizes Deployment Time','','Moderate'"),
		ontEntryInsert("37,'reduces software production time','','Moderate'"),
		ontEntryInsert("38,'reduces customer evaluation time','','Moderate'"),
		ontEntryInsert("39,'Operating Cost','What are the operating costs?','Moderate'"),
		ontEntryInsert("40,'Minimizes Communication Cost','','Moderate'"),
		ontEntryInsert("41,'Maintenance Cost','Cost to maintain the system once deployed','Moderate'"),
		ontEntryInsert("42,'Reduces Maintenance Time','','Moderate'"),
		ontEntryInsert("43,'reduces re-compilation','','Moderate'"),
		ontEntryInsert("44,'Reduces Support Cost','','Moderate'"),
		ontEntryInsert("45,'increases hardware support available','','Moderate'"),
		ontEntryInsert("46,'increases software support available','','Moderate'"),
		ontEntryInsert("47,'Reduces Re-engineering Cost','','Moderate'"),
		ontEntryInsert("48,'Reduces Retirement Cost','','Moderate'"),
		ontEntryInsert("49,'Upgrade Cost','How expensive is it to keep the system up to date?','Moderate'"),
		ontEntryInsert("50,'Reduces COTS Risk','','Moderate'"),
		ontEntryInsert("51,'isolates code dependent on outside software','','Moderate'"),
		ontEntryInsert("52,'reduces vendor dependencies','','Moderate'"),
		ontEntryInsert("53,'reduces version dependencies','','Moderate'"),
		ontEntryInsert("54,'isolates version dependencies','','Moderate'"),
		ontEntryInsert("55,'Administration Cost','How expensive is administration?','Moderate'"),
		ontEntryInsert("56,'Reduces Coordination Cost','','Moderate'"),
		ontEntryInsert("57,'reduces coordination time','','Moderate'"),
		ontEntryInsert("58,'Reduces Planning Cost','','Moderate'"),
		ontEntryInsert("59,'reduces planning time','','Moderate'"),
		ontEntryInsert("60,'Reduces Project Tracking Cost','','Moderate'"),
		ontEntryInsert("61,'Reduces Process Management Cost','','Moderate'"),
		ontEntryInsert("62,'reduces process management time','','Moderate'"),
		ontEntryInsert("63,'Adaptability Criteria','','Moderate'"),
		ontEntryInsert("64,'Extensibility','','Moderate'"),
		ontEntryInsert("65,'Minimizes Modification Impact','','Moderate'"),
		ontEntryInsert("66,'isolates likely to change code','','Moderate'"),
		ontEntryInsert("67,'reduces modification impact','','Moderate'"),
		ontEntryInsert("68,'reduces change coordination','','Moderate'"),
		ontEntryInsert("69,'facilitates wrappability','','Moderate'"),
		ontEntryInsert("70,'uses replaceable modules','','Moderate'"),
		ontEntryInsert("71,'Minimizes the Amount of Code to Modify','','Moderate'"),
		ontEntryInsert("72,'increases commonality','','Moderate'"),
		ontEntryInsert("73,'reduces coupling','','Moderate'"),
		ontEntryInsert("74,'increases encapsulation','','Moderate'"),
		ontEntryInsert("75,'increases cohesion','','Moderate'"),
		ontEntryInsert("76,'Simplifies Modification','','Moderate'"),
		ontEntryInsert("77,'uses a design pattern','','Moderate'"),
		ontEntryInsert("78,'reduces duplication','','Moderate'"),
		ontEntryInsert("79,'provides modularity','','Moderate'"),
		ontEntryInsert("80,'provides information hiding','','Moderate'"),
		ontEntryInsert("81,'Modifiability','','Moderate'"),
		ontEntryInsert("82,'Increases Flexibility','','Moderate'"),
		ontEntryInsert("83,'{provides | supports} reflection','','Moderate'"),
		ontEntryInsert("84,'provides tunable parameters','','Moderate'"),
		ontEntryInsert("85,'Adaptability','','Moderate'"),
		ontEntryInsert("86,'Increases Additivity','','Moderate'"),
		ontEntryInsert("87,'Increases Elasticity','','Moderate'"),
		ontEntryInsert("88,'Increases','','Moderate'"),
		ontEntryInsert("89,'Portability','','Moderate'"),
		ontEntryInsert("90,'Reduces Hardware Dependencies','','Moderate'"),
		ontEntryInsert("91,'isolates hardware dependent code','','Moderate'"),
		ontEntryInsert("92,'Reduces Software Dependencies','','Moderate'"),
		ontEntryInsert("93,'{avoids | reduces} OS dependencies','','Moderate'"),
		ontEntryInsert("94,'Scalability','','Moderate'"),
		ontEntryInsert("95,'Increases Scalability','','Moderate'"),
		ontEntryInsert("96,'{allows | supports} additional users','','Moderate'"),
		ontEntryInsert("97,'{provides | supports} policy/mechanism separation','','Moderate'"),
		ontEntryInsert("98,'adapts to increase in intensity of use','','Moderate'"),
		ontEntryInsert("99,'minimizes connections to be set up','','Moderate'"),
		ontEntryInsert("100,'supports functionality reuse','','Moderate'"),
		ontEntryInsert("101,'avoids fixed data sizes','','Moderate'"),
		ontEntryInsert("102,'Reusability','','Moderate'"),
		ontEntryInsert("103,'Interoperability','','Moderate'"),
		ontEntryInsert("104,'Provides Interface Standardization','','Moderate'"),
		ontEntryInsert("105,'{is a | uses a} defined interface','','Moderate'"),
		ontEntryInsert("106,'{is a | uses a} standard interface','','Moderate'"),
		ontEntryInsert("107,'conforms to an API','','Moderate'"),
		ontEntryInsert("108,'{provides | supports}  consistent interfaces','','Moderate'"),
		ontEntryInsert("109,'{is a | conforms to a} standard protocol','','Moderate'"),
		ontEntryInsert("110,'Supports Easier Integration','','Moderate'"),
		ontEntryInsert("111,'exposes the API','','Moderate'"),
		ontEntryInsert("112,'reduces shared data','','Moderate'"),
		ontEntryInsert("113,'provides compatability','','Moderate'"),
		ontEntryInsert("114,'Dependability Criteria','','Moderate'"),
		ontEntryInsert("115,'Security','','Moderate'"),
		ontEntryInsert("116,'Provides Access Control','','Moderate'"),
		ontEntryInsert("117,'require authorization','','Moderate'"),
		ontEntryInsert("118,'{provides | supports} multiple authorization/access levels','','Moderate'"),
		ontEntryInsert("119,'{provides | supports} mandatory access controls','','Moderate'"),
		ontEntryInsert("120,'{provides | supports} discretionary access controls','','Moderate'"),
		ontEntryInsert("121,'Increases Data Security','','Moderate'"),
		ontEntryInsert("122,'{provides | supports} data encryption','','Moderate'"),
		ontEntryInsert("123,'{provides | supports} network isolation','','Moderate'"),
		ontEntryInsert("124,'Responds to Threats','','Moderate'"),
		ontEntryInsert("125,'{provides | supports} countermeasures','','Moderate'"),
		ontEntryInsert("126,'prevents denial of service','','Moderate'"),
		ontEntryInsert("127,'{provides | supports} threat detection','','Moderate'"),
		ontEntryInsert("128,'{provides | supports} threat prevention','','Moderate'"),
		ontEntryInsert("129,'{provides | supports} threat recovery','','Moderate'"),
		ontEntryInsert("130,'Robustness','','Moderate'"),
		ontEntryInsert("131,'Responds to User Error','','Moderate'"),
		ontEntryInsert("132,'prevents user error','','Moderate'"),
		ontEntryInsert("133,'minimizes user error','','Moderate'"),
		ontEntryInsert("134,'detects user error','','Moderate'"),
		ontEntryInsert("135,'recovers from user error','','Moderate'"),
		ontEntryInsert("136,'requests action confirmation','','Moderate'"),
		ontEntryInsert("137,'requires action confirmation','','Moderate'"),
		ontEntryInsert("138,'Fault Tolerance','','Moderate'"),
		ontEntryInsert("139,'Handles Faults','','Moderate'"),
		ontEntryInsert("140,'{provides | supports} graceful degredation','','Moderate'"),
		ontEntryInsert("141,'{provides | supports} replication','','Moderate'"),
		ontEntryInsert("142,'{provides | supports} failover','','Moderate'"),
		ontEntryInsert("143,'{provides | supports} fault masking','','Moderate'"),
		ontEntryInsert("144,'{provides | supports} retry when failure','','Moderate'"),
		ontEntryInsert("145,'{provides | supports} restart when failure','','Moderate'"),
		ontEntryInsert("146,'{provides | supports} reconfigure when failure','','Moderate'"),
		ontEntryInsert("147,'{provides | supports} failure repair','','Moderate'"),
		ontEntryInsert("148,'provides recovery blocks','','Moderate'"),
		ontEntryInsert("149,'Tolerates Threats','','Moderate'"),
		ontEntryInsert("150,'{provides | supports} data recoverability','','Moderate'"),
		ontEntryInsert("151,'{provides | supports} state recoverability','','Moderate'"),
		ontEntryInsert("152,'{provides | supports} fault detection','','Moderate'"),
		ontEntryInsert("153,'{provides | supports }fault confinement','','Moderate'"),
		ontEntryInsert("154,'Reliability','','Moderate'"),
		ontEntryInsert("155,'Prevents Data Loss','','Moderate'"),
		ontEntryInsert("156,'{is a | supports a} reliable protocol','','Moderate'"),
		ontEntryInsert("157,'prevents data overwrites','','Moderate'"),
		ontEntryInsert("158,'Safety','','Moderate'"),
		ontEntryInsert("159,'Increases Maturity','','Moderate'"),
		ontEntryInsert("160,'{is an | uses an} evaluated technology','','Moderate'"),
		ontEntryInsert("161,'Increases Predictability','','Moderate'"),
		ontEntryInsert("162,'provides stability','','Moderate'"),
		ontEntryInsert("163,'provides a contract','','Moderate'"),
		ontEntryInsert("164,'Availability','','Moderate'"),
		ontEntryInsert("165,'Reduces Error Rates','','Moderate'"),
		ontEntryInsert("166,'End User Criteria','','Moderate'"),
		ontEntryInsert("167,'Usability','','Moderate'"),
		ontEntryInsert("168,'Increases Physical Ease of Use','','Moderate'"),
		ontEntryInsert("169,'{provides | supports} effective use of screen real-estate','','Moderate'"),
		ontEntryInsert("170,'minimizes keystrokes','','Moderate'"),
		ontEntryInsert("171,'{provides | supports} increased visual contrast','','Moderate'"),
		ontEntryInsert("172,'is easy to read','','Moderate'"),
		ontEntryInsert("173,'Increases Cognitive Ease of Use','','Moderate'"),
		ontEntryInsert("174,'provides reasonable default values','','Moderate'"),
		ontEntryInsert("175,'provides user guidance','','Moderate'"),
		ontEntryInsert("176,'{encourages | supports} direct manipulation','','Moderate'"),
		ontEntryInsert("177,'minimizes memory load on user','','Moderate'"),
		ontEntryInsert("178,'provides feedback','','Moderate'"),
		ontEntryInsert("179,'{conforms to | utilizes} user experience','','Moderate'"),
		ontEntryInsert("180,'increases visibility of function to users','','Moderate'"),
		ontEntryInsert("181,'uses predictable sequences','','Moderate'"),
		ontEntryInsert("182,'intuitiveness','','Moderate'"),
		ontEntryInsert("183,'{provides a | supports a} appropriate metaphor','','Moderate'"),
		ontEntryInsert("184,'Increases Interface Consistency','','Moderate'"),
		ontEntryInsert("185,'{provides | supports} data entry consistency','','Moderate'"),
		ontEntryInsert("186,'{provides | supports} data display consistency','','Moderate'"),
		ontEntryInsert("187,'{provides | supports} color and style consistency','','Moderate'"),
		ontEntryInsert("188,'Increases Recoverability','','Moderate'"),
		ontEntryInsert("189,'supports undo of user actions','','Moderate'"),
		ontEntryInsert("190,'corrects user errors','','Moderate'"),
		ontEntryInsert("191,'Increases Learnability','','Moderate'"),
		ontEntryInsert("192,'Increases Acceptability','','Moderate'"),
		ontEntryInsert("193,'increases aesthetic value','','Moderate'"),
		ontEntryInsert("194,'avoides offensiveness','','Moderate'"),
		ontEntryInsert("195,'Provides User Customization','','Moderate'"),
		ontEntryInsert("196,'{provides | supports} customization','','Moderate'"),
		ontEntryInsert("197,'supports different levels of user expertise','','Moderate'"),
		ontEntryInsert("198,'Supports Internationalization','','Moderate'"),
		ontEntryInsert("199,'reduces cultural dependencies','','Moderate'"),
		ontEntryInsert("200,'Increases Accessibility','','Moderate'"),
		ontEntryInsert("201,'visual accessibility','','Moderate'"),
		ontEntryInsert("202,'auditory accessibility','','Moderate'"),
		ontEntryInsert("203,'mobility accessibility','','Moderate'"),
		ontEntryInsert("204,'cogntivie accessibility','','Moderate'"),
		ontEntryInsert("205,'Integrity','','Moderate'"),
		ontEntryInsert("206,'Increases Completeness','','Moderate'"),
		ontEntryInsert("207,'Increases Consistency','','Moderate'"),
		ontEntryInsert("208,'{provides | supports} internal consistency','','Moderate'"),
		ontEntryInsert("209,'{provides | supports} external consistency','','Moderate'"),
		ontEntryInsert("210,'Increases Accuracy','','Moderate'"),
		ontEntryInsert("211,'{provides | supports} exception handling','','Moderate'"),
		ontEntryInsert("212,'supports resource assignment','','Moderate'"),
		ontEntryInsert("213,'provides validation','','Moderate'"),
		ontEntryInsert("214,'provides justification enforcement','','Moderate'"),
		ontEntryInsert("215,'provides verification','','Moderate'"),
		ontEntryInsert("216,'{supports | provides} a checkpoint','','Moderate'"),
		ontEntryInsert("217,'{supports | provides} better information flow','','Moderate'"),
		ontEntryInsert("218,'{supports | provides} authentication enforcement','','Moderate'"),
		ontEntryInsert("219,'{supports | provides} auditing','','Moderate'"),
		ontEntryInsert("220,'{supports | provides} consistency checking','','Moderate'"),
		ontEntryInsert("221,'requests confirmation','','Moderate'"),
		ontEntryInsert("222,'performs cross examination','','Moderate'"),
		ontEntryInsert("223,'provides tracking assistance','','Moderate'"),
		ontEntryInsert("224,'provides certification','','Moderate'"),
		ontEntryInsert("225,'requests authorization','','Moderate'"),
		ontEntryInsert("226,'provides precision','','Moderate'"),
		ontEntryInsert("227,'Needs Satisfaction Criteria','','Moderate'"),
		ontEntryInsert("228,'Verifiability','','Moderate'"),
		ontEntryInsert("229,'Increases Testability','','Moderate'"),
		ontEntryInsert("230,'increases visibility of function to be evaluated','','Moderate'"),
		ontEntryInsert("231,'supports instrumentation','','Moderate'"),
		ontEntryInsert("232,'provides re-entry points','','Moderate'"),
		ontEntryInsert("233,'provides triggers','Triggers are set up to detect when data has changed.','Moderate'"),
		ontEntryInsert("234,'minimizes variable reuse','','Moderate'"),
		ontEntryInsert("235,'supports internal information capture','','Moderate'"),
		ontEntryInsert("236,'facilitates repeatability','','Moderate'"),
		ontEntryInsert("237,'Increases Auditability','','Moderate'"),
		ontEntryInsert("238,'Maintainability Criteria','','Moderate'"),
		ontEntryInsert("239,'Readability','','Moderate'"),
		ontEntryInsert("240,'Increases Code Understandability','','Moderate'"),
		ontEntryInsert("241,'provides good documentation','','Moderate'"),
		ontEntryInsert("242,'{provides  | supports} code consistency','','Moderate'"),
		ontEntryInsert("243,'{provides | supports} consistent method naming','','Moderate'"),
		ontEntryInsert("244,'{provides | supports} code readability','','Moderate'"),
		ontEntryInsert("245,'{provides | supports} decomposability','','Moderate'"),
		ontEntryInsert("246,'Supportability','','Moderate'"),
		ontEntryInsert("247,'Traceability','','Moderate'"),
		ontEntryInsert("248,'Performance Criteria','','Moderate'"),
		ontEntryInsert("249,'Response Time and Throughput','','Moderate'"),
		ontEntryInsert("250,'Increases Speed','','Moderate'"),
		ontEntryInsert("251,'{provides | supports} distribution','','Moderate'"),
		ontEntryInsert("252,'{provides | supports} parallelism','','Moderate'"),
		ontEntryInsert("253,'{provides | supports} congestion control','','Moderate'"),
		ontEntryInsert("254,'{provides | supports} efficient resource scheduling','','Moderate'"),
		ontEntryInsert("255,'{provides | supports} caching','','Moderate'"),
		ontEntryInsert("256,'{provides | supports} load shedding','','Moderate'"),
		ontEntryInsert("257,'{provides | supports} multi-threading','','Moderate'"),
		ontEntryInsert("258,'{is a | uses a} fast language','','Moderate'"),
		ontEntryInsert("259,'{is a | uses a} efficient algorithm','','Moderate'"),
		ontEntryInsert("260,'Optimizes Resource Use','','Moderate'"),
		ontEntryInsert("261,'{provides | supports} increased component capacity','','Moderate'"),
		ontEntryInsert("262,'reduces component load','','Moderate'"),
		ontEntryInsert("263,'minimizes bandwidth','','Moderate'"),
		ontEntryInsert("264,'minimizes persistent storage','','Moderate'"),
		ontEntryInsert("265,'{provides | supports} bandwidth change adaptation','','Moderate'"),
		ontEntryInsert("266,'Minimizes Resource Conflicts','','Moderate'"),
		ontEntryInsert("267,'avoids deadlock','','Moderate'"),
		ontEntryInsert("268,'avoids starvation','','Moderate'"),
		ontEntryInsert("269,'minimizes contention','','Moderate'"),
		ontEntryInsert("270,'Reduces Latency','','Moderate'"),
		ontEntryInsert("271,'{provides | supports} increased processing speed','','Moderate'"),
		ontEntryInsert("272,'decreases latency/perceived delay','','Moderate'"),
		ontEntryInsert("273,'Memory Efficiency','','Moderate'"),
		ontEntryInsert("274,'Minimizes Memory Use','','Moderate'"),
		ontEntryInsert("275,'avoids paging','','Moderate'"),
		ontEntryInsert("276,'prevenst memory leaks','','Moderate'"),
		ontEntryInsert("277,'minimizes secondary storage use','','Moderate'")
	};
	
	/**
	 * String array containing values to be inserted into ontRelationships.
	 */
	
	private static String INSERT_ONTRELATIONSHIPS[] =
	{
		ontRelationshipInsert("1,2"),
		ontRelationshipInsert("2,3"),
		ontRelationshipInsert("3,4"),
		ontRelationshipInsert("4,5"),
		ontRelationshipInsert("4,6"),
		ontRelationshipInsert("3,7"),
		ontRelationshipInsert("7,8"),
		ontRelationshipInsert("7,9"),
		ontRelationshipInsert("7,10"),
		ontRelationshipInsert("7,11"),
		ontRelationshipInsert("3,12"),
		ontRelationshipInsert("12,13"),
		ontRelationshipInsert("12,14"),
		ontRelationshipInsert("12,15"),
		ontRelationshipInsert("12,16"),
		ontRelationshipInsert("12,17"),
		ontRelationshipInsert("3,18"),
		ontRelationshipInsert("18,19"),
		ontRelationshipInsert("18,20"),
		ontRelationshipInsert("3,21"),
		ontRelationshipInsert("21,22"),
		ontRelationshipInsert("3,23"),
		ontRelationshipInsert("23,24"),
		ontRelationshipInsert("3,25"),
		ontRelationshipInsert("25,26"),
		ontRelationshipInsert("3,27"),
		ontRelationshipInsert("27,28"),
		ontRelationshipInsert("3,29"),
		ontRelationshipInsert("29,30"),
		ontRelationshipInsert("2,31"),
		ontRelationshipInsert("31,32"),
		ontRelationshipInsert("32,33"),
		ontRelationshipInsert("31,34"),
		ontRelationshipInsert("34,35"),
		ontRelationshipInsert("31,36"),
		ontRelationshipInsert("36,37"),
		ontRelationshipInsert("36,38"),
		ontRelationshipInsert("2,39"),
		ontRelationshipInsert("39,40"),
		ontRelationshipInsert("2,41"),
		ontRelationshipInsert("41,42"),
		ontRelationshipInsert("42,43"),
		ontRelationshipInsert("41,44"),
		ontRelationshipInsert("44,45"),
		ontRelationshipInsert("44,46"),
		ontRelationshipInsert("41,47"),
		ontRelationshipInsert("41,48"),
		ontRelationshipInsert("2,49"),
		ontRelationshipInsert("49,50"),
		ontRelationshipInsert("50,51"),
		ontRelationshipInsert("50,52"),
		ontRelationshipInsert("50,53"),
		ontRelationshipInsert("50,54"),
		ontRelationshipInsert("2,55"),
		ontRelationshipInsert("55,56"),
		ontRelationshipInsert("56,57"),
		ontRelationshipInsert("55,58"),
		ontRelationshipInsert("58,59"),
		ontRelationshipInsert("55,60"),
		ontRelationshipInsert("55,61"),
		ontRelationshipInsert("61,62"),
		ontRelationshipInsert("1,63"),
		ontRelationshipInsert("63,64"),
		ontRelationshipInsert("64,65"),
		ontRelationshipInsert("65,66"),
		ontRelationshipInsert("65,67"),
		ontRelationshipInsert("65,68"),
		ontRelationshipInsert("65,69"),
		ontRelationshipInsert("65,70"),
		ontRelationshipInsert("64,71"),
		ontRelationshipInsert("71,72"),
		ontRelationshipInsert("71,73"),
		ontRelationshipInsert("71,74"),
		ontRelationshipInsert("71,75"),
		ontRelationshipInsert("64,76"),
		ontRelationshipInsert("76,77"),
		ontRelationshipInsert("76,78"),
		ontRelationshipInsert("76,79"),
		ontRelationshipInsert("76,80"),
		ontRelationshipInsert("63,81"),
		ontRelationshipInsert("81,82"),
		ontRelationshipInsert("82,83"),
		ontRelationshipInsert("82,84"),
		ontRelationshipInsert("63,85"),
		ontRelationshipInsert("85,86"),
		ontRelationshipInsert("85,87"),
		ontRelationshipInsert("85,88"),
		ontRelationshipInsert("63,89"),
		ontRelationshipInsert("89,90"),
		ontRelationshipInsert("90,91"),
		ontRelationshipInsert("89,92"),
		ontRelationshipInsert("92,93"),
		ontRelationshipInsert("63,94"),
		ontRelationshipInsert("94,95"),
		ontRelationshipInsert("95,96"),
		ontRelationshipInsert("95,97"),
		ontRelationshipInsert("95,98"),
		ontRelationshipInsert("95,99"),
		ontRelationshipInsert("95,100"),
		ontRelationshipInsert("95,101"),
		ontRelationshipInsert("63,102"),
		ontRelationshipInsert("63,103"),
		ontRelationshipInsert("103,104"),
		ontRelationshipInsert("104,105"),
		ontRelationshipInsert("104,106"),
		ontRelationshipInsert("104,107"),
		ontRelationshipInsert("104,108"),
		ontRelationshipInsert("104,109"),
		ontRelationshipInsert("103,110"),
		ontRelationshipInsert("110,111"),
		ontRelationshipInsert("110,112"),
		ontRelationshipInsert("110,113"),
		ontRelationshipInsert("1,114"),
		ontRelationshipInsert("114,115"),
		ontRelationshipInsert("115,116"),
		ontRelationshipInsert("116,117"),
		ontRelationshipInsert("116,118"),
		ontRelationshipInsert("116,119"),
		ontRelationshipInsert("116,120"),
		ontRelationshipInsert("115,121"),
		ontRelationshipInsert("121,122"),
		ontRelationshipInsert("121,123"),
		ontRelationshipInsert("115,124"),
		ontRelationshipInsert("124,125"),
		ontRelationshipInsert("124,126"),
		ontRelationshipInsert("124,127"),
		ontRelationshipInsert("124,128"),
		ontRelationshipInsert("124,129"),
		ontRelationshipInsert("114,130"),
		ontRelationshipInsert("130,131"),
		ontRelationshipInsert("131,132"),
		ontRelationshipInsert("131,133"),
		ontRelationshipInsert("131,134"),
		ontRelationshipInsert("131,135"),
		ontRelationshipInsert("131,136"),
		ontRelationshipInsert("131,137"),
		ontRelationshipInsert("114,138"),
		ontRelationshipInsert("138,139"),
		ontRelationshipInsert("139,140"),
		ontRelationshipInsert("139,141"),
		ontRelationshipInsert("139,142"),
		ontRelationshipInsert("139,143"),
		ontRelationshipInsert("139,144"),
		ontRelationshipInsert("139,145"),
		ontRelationshipInsert("139,146"),
		ontRelationshipInsert("139,147"),
		ontRelationshipInsert("139,148"),
		ontRelationshipInsert("138,149"),
		ontRelationshipInsert("149,150"),
		ontRelationshipInsert("149,151"),
		ontRelationshipInsert("149,152"),
		ontRelationshipInsert("149,153"),
		ontRelationshipInsert("114,154"),
		ontRelationshipInsert("154,155"),
		ontRelationshipInsert("155,156"),
		ontRelationshipInsert("155,157"),
		ontRelationshipInsert("114,158"),
		ontRelationshipInsert("158,159"),
		ontRelationshipInsert("159,160"),
		ontRelationshipInsert("158,161"),
		ontRelationshipInsert("161,162"),
		ontRelationshipInsert("161,163"),
		ontRelationshipInsert("114,164"),
		ontRelationshipInsert("164,165"),
		ontRelationshipInsert("1,166"),
		ontRelationshipInsert("166,167"),
		ontRelationshipInsert("167,168"),
		ontRelationshipInsert("168,169"),
		ontRelationshipInsert("168,170"),
		ontRelationshipInsert("168,171"),
		ontRelationshipInsert("168,172"),
		ontRelationshipInsert("167,173"),
		ontRelationshipInsert("173,174"),
		ontRelationshipInsert("173,175"),
		ontRelationshipInsert("173,176"),
		ontRelationshipInsert("173,177"),
		ontRelationshipInsert("173,178"),
		ontRelationshipInsert("173,179"),
		ontRelationshipInsert("173,180"),
		ontRelationshipInsert("173,181"),
		ontRelationshipInsert("173,182"),
		ontRelationshipInsert("173,183"),
		ontRelationshipInsert("167,184"),
		ontRelationshipInsert("184,185"),
		ontRelationshipInsert("184,186"),
		ontRelationshipInsert("184,187"),
		ontRelationshipInsert("167,188"),
		ontRelationshipInsert("188,189"),
		ontRelationshipInsert("188,190"),
		ontRelationshipInsert("188,132"),
		ontRelationshipInsert("167,191"),
		ontRelationshipInsert("167,192"),
		ontRelationshipInsert("192,193"),
		ontRelationshipInsert("192,194"),
		ontRelationshipInsert("167,195"),
		ontRelationshipInsert("195,196"),
		ontRelationshipInsert("195,197"),
		ontRelationshipInsert("167,198"),
		ontRelationshipInsert("198,199"),
		ontRelationshipInsert("167,200"),
		ontRelationshipInsert("200,201"),
		ontRelationshipInsert("200,202"),
		ontRelationshipInsert("200,203"),
		ontRelationshipInsert("200,204"),
		ontRelationshipInsert("166,205"),
		ontRelationshipInsert("205,206"),
		ontRelationshipInsert("205,207"),
		ontRelationshipInsert("207,208"),
		ontRelationshipInsert("207,209"),
		ontRelationshipInsert("205,210"),
		ontRelationshipInsert("210,211"),
		ontRelationshipInsert("210,212"),
		ontRelationshipInsert("210,213"),
		ontRelationshipInsert("210,214"),
		ontRelationshipInsert("210,215"),
		ontRelationshipInsert("210,216"),
		ontRelationshipInsert("210,217"),
		ontRelationshipInsert("210,218"),
		ontRelationshipInsert("210,219"),
		ontRelationshipInsert("210,220"),
		ontRelationshipInsert("210,221"),
		ontRelationshipInsert("210,222"),
		ontRelationshipInsert("210,223"),
		ontRelationshipInsert("210,224"),
		ontRelationshipInsert("210,225"),
		ontRelationshipInsert("210,226"),
		ontRelationshipInsert("1,227"),
		ontRelationshipInsert("227,228"),
		ontRelationshipInsert("228,229"),
		ontRelationshipInsert("229,230"),
		ontRelationshipInsert("229,231"),
		ontRelationshipInsert("229,232"),
		ontRelationshipInsert("229,233"),
		ontRelationshipInsert("229,234"),
		ontRelationshipInsert("229,235"),
		ontRelationshipInsert("229,236"),
		ontRelationshipInsert("228,237"),
		ontRelationshipInsert("1,238"),
		ontRelationshipInsert("238,239"),
		ontRelationshipInsert("239,240"),
		ontRelationshipInsert("240,241"),
		ontRelationshipInsert("240,242"),
		ontRelationshipInsert("240,243"),
		ontRelationshipInsert("240,244"),
		ontRelationshipInsert("240,245"),
		ontRelationshipInsert("238,246"),
		ontRelationshipInsert("238,247"),
		ontRelationshipInsert("1,248"),
		ontRelationshipInsert("248,249"),
		ontRelationshipInsert("249,250"),
		ontRelationshipInsert("250,251"),
		ontRelationshipInsert("250,252"),
		ontRelationshipInsert("250,253"),
		ontRelationshipInsert("250,254"),
		ontRelationshipInsert("250,255"),
		ontRelationshipInsert("250,256"),
		ontRelationshipInsert("250,257"),
		ontRelationshipInsert("250,258"),
		ontRelationshipInsert("250,259"),
		ontRelationshipInsert("249,260"),
		ontRelationshipInsert("260,261"),
		ontRelationshipInsert("260,262"),
		ontRelationshipInsert("260,263"),
		ontRelationshipInsert("260,264"),
		ontRelationshipInsert("260,265"),
		ontRelationshipInsert("249,266"),
		ontRelationshipInsert("266,267"),
		ontRelationshipInsert("266,268"),
		ontRelationshipInsert("266,269"),
		ontRelationshipInsert("249,270"),
		ontRelationshipInsert("270,271"),
		ontRelationshipInsert("270,272"),
		ontRelationshipInsert("248,273"),
		ontRelationshipInsert("273,274"),
		ontRelationshipInsert("274,275"),
		ontRelationshipInsert("274,276"),
		ontRelationshipInsert("274,277")
	};
	
	/**
	 * Returns the altconstrel table statement.
	 * @return String containing the statement
	 */
	public static String CREATE_ALTCONSTREL() {
		return beginTable("altconstrel")
		+ tablePart("id INTEGER default NULL")
		+ tablePart("name varchar(255) default NULL")
		+ tablePart("alternative INTEGER  default NULL")
		+ tablePart("constr INTEGER  default NULL")
		+ tablePart("units varchar(255) default NULL")
		+ endTable("amount float  default NULL");
	}

	/**
	 * Returns the alternatives table statement.
	 * @return String containing the statement
	 */
	public static String CREATE_ALTERNATIVES() {
		return beginTable("alternatives")
		+ tablePart("id INTEGER NOT NULL " + autoIncrement())
		+ tablePart("name varchar(255) default NULL")
		+ tablePart("description varchar(255) default NULL")
		+ tablePart("status varchar(255)")
		+ tablePart("evaluation float default NULL")
		+ tablePart("artifact varchar(80) default NULL")
		+ tablePart("ptype varchar(255) default NULL")
		+ tablePart("parent INTEGER  default NULL")
		+ tablePart("designType INTEGER  default NULL")
		+ tablePart("designer INTEGER  default NULL")
		+ tablePart("patternid INTEGER default -1")
		+ endTable("PRIMARY KEY  (id)");
	}
	
	/**
	 * Returns the alternativepatterns table statement.
	 * @return String containing the statement
	 */
	public static String CREATE_ALTERNATIVEPATTERNS() {
		return beginTable("alternativepatterns")
		+ tablePart("id INTEGER NOT NULL " + autoIncrement())
		+ tablePart("name varchar(255) default NULL")
		+ tablePart("status varchar(255) default 'At_Issue'")
		+ tablePart("evaluation float default NULL")
		+ tablePart("ptype varchar(255) default NULL")
		+ tablePart("parent INTEGER  default NULL")
		+ tablePart("designType INTEGER  default NULL")
		+ tablePart("designer INTEGER  default NULL")
		+ tablePart("isExactMatch varchar(10) default 'true'")
		+ endTable("PRIMARY KEY  (id)");
	}

	/**
	 * Returns the areaexp table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_AREAEXP() {
		return beginTable("areaexp")
		+ tablePart("id INTEGER NOT NULL " + autoIncrement())
		+ tablePart("name varchar(255) default NULL")
		+ tablePart("des INTEGER  default NULL")
		+ tablePart("area INTEGER  default NULL")
		+ tablePart("level INTEGER  default NULL")
		+ endTable("PRIMARY KEY  (id)");
	}

	/**
	 * Returns the arguments table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_ARGUMENTS() {
		return beginTable("arguments")
		+ tablePart("id INTEGER NOT NULL " + autoIncrement())
		+ tablePart("name varchar(255) default NULL")
		+ tablePart("description varchar(255) default NULL")
		+ tablePart("ptype varchar(255) default NULL")
		+ tablePart("parent INTEGER  default NULL")
		+ tablePart("type varchar(255) default NULL")
		+ tablePart("plausibility varchar(255) default NULL")
		+ tablePart("importance varchar(255) default NULL")
		+ tablePart("amount INTEGER  default NULL")
		+ tablePart("argtype varchar(255) default NULL")
		+ tablePart("claim INTEGER  default NULL")
		+ tablePart("alternative INTEGER  default NULL")
		+ tablePart("requirement INTEGER  default NULL")
		+ tablePart("assumption INTEGER  default NULL")
		+ tablePart("designer INTEGER  default NULL")
		+ endTable("PRIMARY KEY  (id)");
	}

	/**
	 * Returns the associations table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_ASSOCIATIONS() {
		return beginTable("associations")
		+ tablePart("alternative INTEGER DEFAULT NULL")
		+ tablePart("artifact varchar(255) default NULL")
		+ tablePart("artresource varchar(120) default NULL")
		+ tablePart("artname varchar(120) default NULL")
		+ endTable("assocmessage varchar(255) default NULL");
	}

	/**
	 * Returns the assumptions table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_ASSUMPTIONS() {
		return beginTable("assumptions")
		+ tablePart("id INTEGER NOT NULL " + autoIncrement())
		+ tablePart("name varchar(255) default NULL")
		+ tablePart("description varchar(255) default NULL")
		+ tablePart("importance varchar(255) default NULL")
		+ tablePart("enabled varchar(255) default NULL")
		+ endTable("PRIMARY KEY  (id)");
	}

	/**
	 * Returns the claims table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_CLAIMS() {
		return beginTable("claims")
		+ tablePart("id INTEGER NOT NULL " + autoIncrement())
		+ tablePart("name varchar(255) default NULL")
		+ tablePart("description varchar(255) default NULL")
		+ tablePart("direction varchar(255) default NULL")
		+ tablePart("importance varchar(255) default NULL")
		+ tablePart("ontology INTEGER  default NULL")
		+ tablePart("enabled varchar(255) default NULL")
		+ endTable("PRIMARY KEY  (id)");
	}

	/**
	 * Returns the condecrelationships table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_CONDECRELATIONSHIPS() {
		return beginTable("condecrelationships")
		+ tablePart("decision INTEGER  default NULL")
		+ endTable("constr INTEGER  default NULL");
	}

	/**
	 * Returns the constraintrelationships table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_CONSTRAINTRELATIONSHIPS() {
		return beginTable("constraintrelationships")
		+ tablePart("parent INTEGER  default NULL")
		+ endTable("child INTEGER  default NULL");
	}

	/**
	 * Returns the constraints table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_CONSTRAINTS() {
		return beginTable("constraints")
		+ tablePart("id INTEGER NOT NULL " + autoIncrement())
		+ tablePart("name varchar(255) default NULL")
		+ tablePart("description varchar(255) default NULL")
		+ tablePart("type INTEGER  default NULL")
		+ tablePart("subsys INTEGER  default NULL")
		+ tablePart("amount INTEGER  default NULL")
		+ endTable("PRIMARY KEY  (id)");
	}

	/**
	 * Returns the XFeature mapping table statement
	 * @return String containing the statement
	 */
	
	public static final String CREATE_XFEATUREMAPPING() {
		return beginTable("xfeaturemapping")
		+ tablePart("id INTEGER NOT NULL " + autoIncrement())
		+ tablePart("ratid INTEGER default NULL")
		+ tablePart("rattype varchar(255) default NULL")
		+ tablePart("nodetype varchar(255) default NULL")
		+ tablePart("nodename varchar(255) default NULL")
		+ tablePart("parent INTEGER default NULL")
		+ endTable("PRIMARY KEY (id)");
	}	
	/**
	 * Returns the contingencies table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_CONTINGENCIES() {
		return beginTable("contingencies")
		+ tablePart("id INTEGER NOT NULL " + autoIncrement())
		+ tablePart("name varchar(255) default NULL")
		+ tablePart("description varchar(255) default NULL")
		+ tablePart("amount float  default NULL")
		+ endTable("PRIMARY KEY  (id)");
	}

	/**
	 * Returns the decisions table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_DECISIONS() {
		return beginTable("decisions")
		+ tablePart("id INTEGER NOT NULL " + autoIncrement())
		+ tablePart("name varchar(255) default NULL")
		+ tablePart("description varchar(255) default NULL")
		+ tablePart("type varchar(255) default NULL")
		+ tablePart("status varchar(255) default NULL")
		+ tablePart("phase varchar(255) default NULL")
		+ tablePart("subdecreq varchar(255) default NULL")
		+ tablePart("ptype varchar(255) default NULL")
		+ tablePart("parent INTEGER  default NULL")
		+ tablePart("subsys INTEGER  default NULL")
		+ tablePart("designer INTEGER  default NULL")
		+ tablePart("patternid INTEGER default -1")
		+ endTable("PRIMARY KEY  (id)");
	}

	/**
	 * Returns the designcomponentrelationships table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_DESIGNCOMPONENTRELATIONSHIPS() {
		return beginTable("designcomponentrelationships")
		+ tablePart("parent INTEGER  default NULL")
		+ endTable("child INTEGER  default NULL");
	}

	/**
	 * Returns the designcomponents table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_DESIGNCOMPONENTS() {
		return beginTable("designcomponents")
		+ tablePart("id INTEGER NOT NULL " + autoIncrement())
		+ tablePart("name varchar(255) default NULL")
		+ tablePart("description varchar(255) default NULL")
		+ endTable("PRIMARY KEY  (id)");
	}

	/**
	 * Returns the designerprofiles table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_DESIGNERPROFILES() {
		return beginTable("designerprofiles")
		+ tablePart("id INTEGER NOT NULL " + autoIncrement())
		+ tablePart("name varchar(255) default NULL")
		+ tablePart("corpPosition varchar(255) default NULL")
		+ tablePart("projPosition varchar(255) default NULL")
		+ tablePart("exprHere INTEGER  default NULL")
		+ tablePart("exprTotal INTEGER  default NULL")
		+ endTable("PRIMARY KEY  (id)");
	}

	/**
	 * Returns the history table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_HISTORY() {
		return beginTable("history")
		+ tablePart("ptype varchar(255) default NULL")
		+ tablePart("parent INTEGER ")
		+ tablePart("date timestamp NOT NULL")
		+ tablePart("reason varchar(255) default NULL")
		+ endTable("status varchar(255) default NULL");
	}

	/**
	 * Returns the ontconrel table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_ONTCONREL() {
		return beginTable("ontconrel")
		+ tablePart("constr INTEGER  default NULL")
		+ endTable("ontEntry INTEGER  default NULL");
	}

	/**
	 * Returns the ontentries table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_ONTENTRIES() {
		return beginTable("ontentries")
		+ tablePart("id INTEGER NOT NULL ")
		+ tablePart("name varchar(255) default NULL")
		+ tablePart("description varchar(255) default NULL")
		+ tablePart("importance varchar(255) default NULL")
		+ endTable("PRIMARY KEY  (id)");
	}

	/**
	 * Returns the ontrelationships table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_ONTRELATIONSHIPS() {
		return beginTable("ontrelationships")
		+ tablePart("parent INTEGER default NULL")
		+ endTable("child INTEGER default NULL");
	}
	
	/**
	 * Generates A Create Statement For A View Of The 
	 * Ontology Relationship. This view potentially doubles or
	 * triples the performance of SelectOntEntry by reducing
	 * the number of table accesses from 3 to 1, however this
	 * is entirely dependant on the DBMS view implementation.
	 * 
	 * @return a string for creating the ontology hierarchy view
	 */
	public static final String CREATEVIEW_ONTRELATIONSHIP_HIERARCHY() {
		return "CREATE VIEW " + RationaleDBUtil.escapeTableName("ONT_HIERARCHY")
			+ " (parentName, childName, parentID, childID) as "
			+ " select parent.name, child.name, parent.id, child.id from "
			+ RationaleDBUtil.escapeTableName("ONTENTRIES") + " parent, "
			+ RationaleDBUtil.escapeTableName("ONTENTRIES") + " child, "
			+ RationaleDBUtil.escapeTableName("ONTRELATIONSHIPS") + " rel "			
			+ "where rel.parent = parent.id and rel.child = child.id";
	}
	/**
	 * Returns the questions table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_QUESTIONS() {
		return beginTable("questions")
		+ tablePart("id INTEGER NOT NULL " + autoIncrement())
		+ tablePart("name varchar(255) default NULL")
		+ tablePart("description varchar(255) default NULL")
		+ tablePart("status varchar(255) default NULL")
		+ tablePart("proc varchar(255) default NULL")
		+ tablePart("answer varchar(255) default NULL")
		+ tablePart("ptype varchar(255) default NULL")
		+ tablePart("parent INTEGER  default NULL")
		+ endTable("PRIMARY KEY  (id)");
	}
	

	/**
	 * Returns the requirements table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_REQUIREMENTS() {
		return beginTable("requirements")
		+ tablePart("id INTEGER NOT NULL " + autoIncrement())
		+ tablePart("name varchar(255) default NULL")
		+ tablePart("description varchar(255) default NULL")
		+ tablePart("type varchar(255) default NULL")
		+ tablePart("artifact varchar(80) default NULL")
		+ tablePart("ptype varchar(255) default NULL")
		+ tablePart("parent INTEGER  default NULL")
		+ tablePart("enabled varchar(255) default NULL")
		+ tablePart("status varchar(255) default NULL")
		+ tablePart("importance varchar(255) default NULL")
		+ tablePart("ontology INTEGER  default NULL")
		+ endTable("PRIMARY KEY  (id)");
	}
	
	/**
	 * Returns the candidate table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_CANDIDATES() {
return beginTable("candidates")
+ tablePart("id INTEGER NOT NULL " + autoIncrement())
+ tablePart("name varchar(255) default NULL")
+ tablePart("description varchar(255) default NULL")
+ tablePart("type varchar(255) default NULL")
+ tablePart("artifact varchar(80) default NULL")
+ tablePart("ptype varchar(255) default NULL")
+ tablePart("parent INTEGER  default NULL")
+ tablePart("source varchar(255) default NULL")
+ tablePart("qualifier varchar(80) default NULL")
+ endTable("PRIMARY KEY  (id)");
	}

	
	/**
	 * Returns the status table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_SOURCES() {
		return beginTable("sources")
		+ tablePart("source VARCHAR(255) default NULL")
		+ tablePart("ptype varchar(255) default NULL")
		+ endTable("parent INTEGER  default NULL");
	}
	
	/**
	 * Returns the status table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_STATUS() {
		return beginTable("status")
		+ tablePart("parent INTEGER")
		+ tablePart("ptype varchar(255) default NULL")
		+ tablePart("date timestamp ")
		+ tablePart("type varchar(255) default NULL")
		+ tablePart("description varchar(255) default NULL")
		+ tablePart("status varchar(255) default NULL")
		+ endTable("override varchar(255) default NULL");
	}

	/**
	 * Returns the tradeoff table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_TRADEOFFS() {
		return beginTable("tradeoffs")
		+ tablePart("id INTEGER NOT NULL " + autoIncrement())
		+ tablePart("name varchar(255) default NULL")
		+ tablePart("description varchar(255) default NULL")
		+ tablePart("ontology1 INTEGER  default NULL")
		+ tablePart("ontology2 INTEGER  default NULL")
		+ tablePart("type varchar(255) default NULL")
		+ tablePart("symmetric varchar(255) default NULL")
		+ endTable("PRIMARY KEY  (id)");
	}

	/**
	 * Returns all of the tables that need to be created.
	 * @return String[] of create table statements
	 */
	private static final String[] CREATE_TABLES() {
		return new String[] {
			CREATE_ALTCONSTREL(),
			CREATE_ALTERNATIVES(),
			CREATE_ALTERNATIVEPATTERNS(),
			CREATE_AREAEXP(),
			CREATE_ARGUMENTS(),
			CREATE_ASSOCIATIONS(),
			CREATE_ASSUMPTIONS(),
			CREATE_CLAIMS(),
			CREATE_CONDECRELATIONSHIPS(),
			CREATE_CONSTRAINTRELATIONSHIPS(),
			CREATE_CONSTRAINTS(),
			CREATE_CONTINGENCIES(),
			CREATE_DECISIONS(),
			CREATE_DESIGNCOMPONENTRELATIONSHIPS(),
			CREATE_DESIGNCOMPONENTS(),
			CREATE_DESIGNERPROFILES(),
			CREATE_HISTORY(),
			CREATE_ONTCONREL(),
			CREATE_ONTENTRIES(),
			CREATE_ONTRELATIONSHIPS(),
			CREATE_QUESTIONS(),
			CREATE_REQUIREMENTS(),
			CREATE_STATUS(),
			CREATE_TRADEOFFS(),
			CREATE_CANDIDATES(),
			CREATE_SOURCES(),
			CREATE_XFEATUREMAPPING(),
			CREATE_PATTERNS(),
			CREATE_PATTERN_DECISION(),
			CREATE_PATTERNDECISIONS(),
			CREATE_PATTERN_ONTENTRIES(),
			CREATE_PATTERNPROBLEMCATEGORIES(),
			CREATE_PATTERN_PROBLEMCATEGORY_RELATIONSHIP()
		};
	};
	
	/**
	 * Gets all of the required SQL statements and returns them.
	 * @return String[] of SQL statements
	 */
	public static String[] getQueries()
	{
		int l_index = 0;
		String l_retval[] = new String
		[
		 	CREATE_TABLES().length 
		];
		
		for( String l_s : CREATE_TABLES() )
		{
			l_retval[l_index] = l_s;
			++l_index;
		}
		return l_retval;
	}
	
	/**
	 * Gets the argument ontology SQL insert statements and returns them.
	 * @return String[] of SQL statements
	 */
	public static String[] getOntologyQueries() {
		int l_index = 0;
		
		String l_retval[] = new String
		[
		 	INSERT_ONTRELATIONSHIPS.length
		 	+ INSERT_ONTENTRY.length
		];
		
		for( String l_s : INSERT_ONTENTRY )
		{
			l_retval[l_index] = l_s;
			++l_index;
		}
		
		for( String l_s : INSERT_ONTRELATIONSHIPS )
		{
			l_retval[l_index] = l_s;
			++l_index;
		}
		return l_retval;
	}
	
	/**
	 * Returns the pattern insert statement
	 * @author wangw2
	 * @param pString name,type,description,problem,context,solution,implementation
	 * @return sql command for inserting the pattern
	 */
	private static String patternInsert(String pString){
		return "INSERT INTO patterns (name, type, description,problem, context, solution, implementation,example,url) values (" + pString + ")";
	}
	
	/**
	 * Returns the pattern decision insert statement
	 * @param pdString name,description,type,status,phase,subdecreq,ptype,parent
	 * @author wangw2
	 * @return sql command for inserting the pattern decisions.
	 */
	private static String patternDecisionInsert(String pdString){
		try{
		int id = RationaleDB.findAvailableID("PatternDecisions");
		return "INSERT INTO patterndecisions (id, name, description, type, status, phase, subdecreq, ptype, parent) values (" + id + ", " + pdString + ")";
		} catch (Exception e){
			
		}
		return "INSERT INTO patterndecisions (id, name, description, type, status, phase, subdecreq, ptype, parent) values (" + pdString + ")";
	}
	
	/**
	 * Returns the pattern Onthology entry insert statement
	 * @param poString patternID, ontID, direction (IS/NOT)
	 * @author wangw2
	 * @return sql command for inserting the onthology insert statement.
	 */
	private static String patternOntEntryInsert(String poString){
		return "INSERT INTO pattern_ontentries (patternID, ontID, direction) values (" + poString + ")";
	}
	
	/**
	 * Returns the pattern decision relationship statement
	 * @param poString patternID, decisionID, parentType
	 * @return
	 */
	private static String patternDecisionRelationshipInsert(String poString){
		return "INSERT INTO pattern_decision (patternID, decisionID, parentType) values (" + poString + ")";
	}
	
	/**
	 * Returns the pattern Problem Category insert statement
	 * @param problemcategory, patterntype
	 * @author wangw2
	 * @return
	 */
	public static String patternProblemCategoriesInsert(String poString){
		
		try{
			int id = RationaleDB.findAvailableID("patternproblemcategories");
			return "INSERT INTO patternproblemcategories (problemcategory, patterntype) values (" + id + "," + poString + ")";
		} catch (Exception e){
			
		}
		return "INSERT INTO patternproblemcategories (problemcategory, patterntype) values (" + poString + ")";
	}
	
	/**
	 * Returns the pattern_problemCategory relationship insert statement
	 * @param patternID, problemcategoryID
	 * @return
	 */
	public static String pattern_problemCategoryInsert(String poString){
		return "INSERT INTO pattern_problemcategory (patternID, problemcategoryID) values (" + poString + ")";
	}
	
	/**
	 * Statements to insert pattern entries in the database
	 */
	private static String[] INSERT_PATTERNS ()
	{
		String toReturn[] = {
		patternInsert("'Three-layer', ‘Architecture’," + "'The system is organized into three primary layers: Presentation, Domain, and Data Source.'" +
				",'In a system in which abstract domains must be implemented in terms of more concrete (less abstract) domains, we need a simple organizational pattern. Additionally, in many systems we need portability of the application to other platforms, or we want to provide an abstract platform or execution environment for which applications may be easily adapted.'" +
				",'Development of a large business application, where many users share common data and operations on them. In addition, there might be legacy systems which have to be integrated in the new application.'" +
				",'Base your layered architecture on three layers: Presentation, Domain, and Data Source.  Presentation layer is about how to handle the interaction between the user and the software. This can be as simple as a command-line or text-based menu system, but these days itís more likely to be a rich-client graphics UI or an HTML-based browser UI. Data source layer is about communicating with other systems that carry out tasks on behalf of the application. These can be transaction monitors, other applications, messaging systems, and so forth. Domain logic, also referred to as business logic. This is the work that this application needs to do for the domain youíre working with. It involves calculations based on inputs and stored data, validation of any data that comes in from the presentation, and figuring out exactly what data source logic to dispatch, depending on commands received from the presentation.'" +
				",' '" +
				",'The three-layer architecture offers significant advantages even for relatively small applications. For instance, the single-user PC application First Account from the Norwegiancompany Economica encapsulates most of the accounting and invoicing functionality in adynamic link library (DLL), which in turn works against a local, flat-file database. This separationenabled the developers with knowledge of accounting and object-oriented design to dedicatethemselves to the central functionality, and user interface designers with little or no knowledge ofprogramming to fully control their part of the application.'" +
				",'http://msdn.microsoft.com/en-us/library/ms978689.aspx'"),
		patternInsert("'Layers', ‘Architecture’,'The Layers architectural pattern helps to structure applications that can be decomposed into groups of subtasks in which each group of subtasks is at a particular level of abstraction.'" +
				",'','','','','','http://vico.org/pages/PatronsDisseny/Pattern%20Layers/'"),
		patternInsert("'Pipes and Filters', ‘Architecture’,'The Pipes and Filters architectural pattern provides a structure for systems that process a stream of data. Each processing step is encapsulated in a filter component. Data is passed through pipes between adjacent filters. Recombining filters allows you to build families of related systems.'" +
				",'','','','','','http://msdn.microsoft.com/en-us/library/ms978599.aspx'"),
		patternInsert("'Blackboard', ‘Architecture’,'The Blackboard architectural pattern is useful for problems for which no deterministic solution strategies are known. In Blackboard several specialized subsystem assemble their knowledge to build a possibly partial or approximate solution.'" +
				",'','','','','','http://www.vico.org/pages/PatronsDisseny/Pattern%20Blackboard/'"),
		patternInsert("'Model-View-Controller', ‘Architecture’,'The MVC architectural pattern divides an interactive application into three components. The model contains the core functionality and data. Views display information to the user. Controllers handle user input. Views and Controllers together comprise the user interface. A change-propagation mechanism ensures consistency between the user interface and the model.'" +
				",'','','','','','http://msdn.microsoft.com/en-us/library/ms978748.aspx'"),
		patternInsert("'Broker', ‘Architecture’,'The Broker architectural pattern can be used to structure distributed software systems with decoupled components that interact by remote service invocations. A broker component is responsible for coordinating communication, such as forwarding requests, as well as for transmitting results and exceptions.'" +
				",'','','','','','http://msdn.microsoft.com/en-us/library/ms978706.aspx'"),
		patternInsert("'Presentation-Abstraction-Control', ‘Architecture’,'The Presentation-Abstraction-Control architectural pattern (PAC) defines a structure for interactive software systems in the form of a hierarchy of cooperating agents. Every agent is responsible for a specific aspect of the applications functionality and consists of three components: presentation, abstraction, and control. This subdivision separates the human-computer interaction aspects of the agent from its functional core and its communication with other agents.'" +
				",'','','','','','http://vico.org/pages/PatronsDisseny/Pattern%20Presentation%20Abstra/'"),
		patternInsert("'Microkernel', ‘Architecture’,'The Microkernel architectural pattern applies to software systems that must be able to adapt to changing system requirements. It separates a minimal functional core from extended functionality and customer-specific parts. '" +
				",'','','','','','http://www.vico.org/pages/PatronsDisseny/Pattern%20MicroKernel/'"),
		patternInsert("'Reflection', ‘Architecture’,'The Reflection architectural pattern provides a mechanism for changing structure and behavior of software systems dynamically. It supports the modification of fundamental aspects, such as type structures and function call mechanisms. In this pattern, an application is split into two parts. A meta level provides information about selected system properties and makes the software self-aware. A base level includes the application logic. Its implementation builds on the meta level. Changes to information kept in the meta level affect subsequent base-level behavior.'" +
				",'','','','','','http://vico.org/pages/PatronsDisseny/Pattern%20Reflection/'"),
		patternInsert("'Whole-Part', ‘Design’,'The Whole-Part design pattern helps with the aggregation of components that together form a semantic unit. An aggregate component, the Whole, encapsulates its constituent components, the Parts, organizes their collaboration, and provides a common interface to its functionality. Direct access to the Parts is not possible.'" +
				",'','','','','','http://www.vico.org/pages/PatronsDisseny/Pattern%20Whole%20Part/index.html'"),
		patternInsert("'Master-Slave', ‘Design’,'The Master-Slave design pattern supports fault tolerance, parallel computation and computational accuracy. A master component distributes work to identical slave components and computes a final result from the results these slaves return.'" +
				",'','','','','','http://www.vico.org/pages/PatronsDisseny/Pattern%20Master%20Slave/'"),
		patternInsert("'Proxy', ‘Design’,'Provide a surrogate or placeholder for another object to control access to it.'" +
				",'','','','','','http://www.vico.org/pages/PatronsDisseny/Pattern%20Broker/'"),
		patternInsert("'Command Processor', ‘Design’,'The Command Processor design pattern separates the request for a service from its execution. A command processor component manages requests as separate objects, schedules their execution, and provides additional services such as the storing of request objects for later undo.'" +
				",'','','','','','http://www.vico.org/pages/PatronsDisseny/Pattern%20Command%20Processor/index.html'"),
		patternInsert("'View Handler', ‘Design’,'The View Handler design pattern helps to manage all views that a software system provides. A view handler component allows clients to open, manipulate and dispose of views. It also coordinates dependencies between view and organizes their update.'" +
				",'','','','','','http://vico.org/pages/PatronsDisseny/Pattern%20View%20Handler/'"),
		patternInsert("'Forward-Receiver', ‘Design’,'The Forwarder-Receiver design pattern provides transparent interprocess communication for software systems with a peer-to-peer interaction model. It introduces forwarders and receivers to decouple peers from the underlying communication mechanisms.'" +
				",'','','','','','http://vico.org/pages/PatronsDisseny/Pattern%20Forward-Receiver/'"),
		patternInsert("'Client-Dispatcher-Server', ‘Design’,'The Client-Dispatcher-Server design pattern introduces an intermediate layer between clients and servers, the dispatcher component. It provides location transparency by means of a name service, and hides the details of the establishment of the communication connection between clients and servers.'" +
				",'','','','','','http://vico.org/pages/PatronsDisseny/Pattern%20ClientDispatcherServer/'"),
		patternInsert("'Publisher-Subscriber', ‘Design’,'The Publisher-Subscriber design pattern helps to keep the state of cooperating components synchronized. To achieve this it enables one-way propagation of changes: one publisher notifies any number of subscribers about changes to its state.'" +
				",'','','','','','http://vico.org/pages/PatronsDisseny/Pattern%20Publisher%20Subscriber/'"),
		patternInsert("'Strategy', ‘Design’,'Define a family of algorithms, encapsulate each one, and make them interchangeable. Strategy lets the algorithm vary independently from clients that use it.'" +
				",'','','','','','http://vico.org/pages/PatronsDisseny/Pattern%20Strategy/'"),
		patternInsert("'Factory', ‘Design’,'Define an interface for creating an object, but let subclasses decide which class to instantiate. Factory Method lets a class defer instantiation to subclasses.'" +
				",'','','','','','http://vico.org/pages/PatronsDisseny/Pattern%20Factory%20Method/'"),
		patternInsert("'Decorator', ‘Design’,'Attach additional responsibilities to an object dynamically. Decorators provide a flexible alternative to subclassing for extending functionality.'" +
				",'','','','','','http://vico.org/pages/PatronsDisseny/Pattern%20Decorator/'"),
		patternInsert("'Composite', ‘Design’,'Compose objects into tree structures to represent part-whole hierarchies. Composite lets clients treat individual objects and compositions of objects uniformly.'" +
				",'','','','','','http://vico.org/pages/PatronsDisseny/Pattern%20Composite/'"),
		patternInsert("'Template Method', ‘Design’,'Define the skeleton of an algorithm in an operation, deferring some steps to subclasses. Template Method lets subclasses redefine certain steps of an algorithm without changing the algorithms structure.'" +
				",'','','','','','http://vico.org/pages/PatronsDisseny/Pattern%20Template%20Method/'"),
		patternInsert("'Command', ‘Design’,'Encapsulate a request as an object, thereby letting you parameterize clients with different requests, queue or log requests, and support undoable operations.'" +
				",'','','','','','http://vico.org/pages/PatronsDisseny/Pattern%20Command/'"),
		patternInsert("'Chain of Responsibility', ‘Design’,'Avoid coupling the sender of a request to its receiver by giving more than one object a chance to handle the request. Chain the receiving objects and pass the request along the chain until an object handles it.'" +
				",'','','','','','http://vico.org/pages/PatronsDisseny/Pattern%20Chain%20of%20Responsability/'"),
		patternInsert("'Facade', ‘Design’,'Provide a unified interface to a set of interfaces in a subsystem. FaÁade defines a higher-level interface that makes the subsystem easier to user.'" +
				",'','','','','','http://vico.org/pages/PatronsDisseny/Pattern%20Facade/'"),
		patternInsert("'Transaction Script', ‘Design’,'Organizes business logic by procedures where each procedure handles a single request from the presentation.'" +
				",'How to organize business logic.','Layers organization',' A Transaction Script is essentially a procedure that takes the input from the presentation, processes it with validations and calculations, stores data in the database, and invokes any operations from other systems. It then replies with more data to the presentation, perhaps doing more calculation to help organize and format the reply. The fundamental organization is of a single procedure for each action that a user might want to do. Hence, we can think of this pattern as being a script for an action, or business transaction. It doesnít have to be a single inline procedure of code. Pieces get separated into subroutines, and these subroutines can be shared between different Transaction Scripts','','','EAA Book Online'"),
		patternInsert("'Domain Model', ‘Design’,'An object model of the domain that incorporates both behavior and data.','','','Using object-oriented way to handle the business logic. Each object takes a part of the logic thatís relevant to it.','','','EAA Book Online'"),
		patternInsert("'Table Module', ‘Design’,'A single instance that handles the business logic for all rows in a database table or view.','','','','','','EAA Book Online'"),
		patternInsert("'Gateway', ‘Design’,'An object that encapsulates access to an external system or resource.','','','','','',''"),
		patternInsert("'Row Data Gateway', ‘Design’,'An object that acts as a Gateway to a single record in a data source. There is one instance per row.','','','','','','EAA Book Online'"),
		patternInsert("'Active Record', ‘Design’,'An object that wraps a row in a database table or view, encapsulates the database access, and adds domain logic on that data.'" +
				",'','','','The essence of an Active Record is a Domain Model in which the classes match very closely the record structure of an underlying database. Each Active Record is responsible for saving and loading to the database and also for any domain logic that acts on the data. This may be all the domain logic in the application, or you may find that some domain logic is held in Transaction Scripts with common and data-oriented code in the Active Record.','','EAA Book Online'"),
		patternInsert("'Table Data Gateway', ‘Design’,'An object that acts as a Gateway to a database table. One instance handles all the rows in the table.A Row Data Gateway gives you objects that look exactly like the record in your record structure but can be accessed with the regular mechanisms of your programming language. All details of data source access are hidden behind this interface.'" +
				",'','','','A Row Data Gateway acts as an object that exactly mimics a single record, such as one database row. In it each column in the database becomes one field. The Row Data Gateway will usually do any type conversion from the data source types to the in-memory types, but this conversion is pretty simple. This pattern holds the data about a row so that a client can then access the Row','','EAA Book Online'"), 		patternInsert("'Application Controller', ‘Design’,'A centralized point for handling screen navigation and the flow of an application.','','','','','','EAA Book Online'"),
		patternInsert("'Transform View', ‘Design’,'A view that processes domain data element by element and transforms it into HTML.','','','','','','EAA Book Online'"),
		patternInsert("'Template View', ‘Design’,'Renders information into HTML by embedding markers in an HTML page.','','','','','','EAA Book Online'"),
		patternInsert("'Two Step View', ‘Design’,'Turns domain data into HTML in two steps: first by forming some kind of logical page, then rendering the logical page into HTML.','','','','','','EAA Book Online'"),
		patternInsert("'Bridge', ‘Design’,'Decouple an abstraction from its implementation so that the two can vary independently.','','','','','','http://vico.org/pages/PatronsDisseny/Pattern%20Bridge/'"),
		patternInsert("'Data Mappter', ‘Design’,'A layer of Mappers that moves data between objects and a database while keeping them independent of each other and the mapper itself.'" +
				",'','Objects and relational databases have different mechanisms for structuring data. Many parts of an object, such as collections and inheritance, arenít present in relational databases. When you build an object model with a lot of business logic itís valuable to use these mechanisms to better organize the data and the behavior that goes with it.  Doing so leads to variant schemas; that is, the object schema and the relational schema donít match up. ','The Data Mapper is a layer of software that separates the in-memory objects from the database. Its responsibility is to transfer data between the two and also to isolate them from each other. With Data Mapper the in-memory objects neednít know even that thereís a database present; they need no SQL interface code, and certainly no knowledge of the database schema. (The database schema is always ignorant of the objects that use it.) Since itís a form of Mapper, Data Mapper itself is even unknown to the domain layer. ','','','http://vico.org/pages/PatronsDisseny/Pattern%20Bridge/'"),
		patternInsert("'Counted Pointer', ‘Idiom’,'This idiom makes memory management of dynamically-allocated shared objects in C++ easier. It introduces a reference counter to a body class that is updated by handle objects. ','','','','','','Book Reference'"),
		patternInsert("'Singleton', ‘Idiom’,'Ensure a class only has one instance, and provide a global point of access to it.','','','','','','http://vico.org/pages/PatronsDisseny/Pattern%20Singleton/'"),
		patternInsert("'Indented Control Flow', ‘Idiom’,'','','','','','','Book Reference'")};
		return toReturn;
	}
	
	/**
	 * Statements to insert pattern decisions for each pattern.
	 */
	private static String[] INSERT_PATTERN_DECISIONS()
	{	
		String[] toReturn = {
		//do not change the order of the following decisions. It's related to the sub candidate patterns
		//three-lay subs
		patternDecisionInsert("'What is the structure of Business Logic Layer', '','SingleChoice','Unresolved','Architecture','No','Pattern',1"),
		patternDecisionInsert("'What is the structure of Data Source Layer', '','SingleChoice','Unresolved','Architecture','No','Pattern',1"),
		patternDecisionInsert("'What is the structure of Presentation Layer', '','SingleChoice','Unresolved','Architecture','No','Pattern',1"),
		
		//layers subs
		patternDecisionInsert("'What is the number of abstraction levels/layers?', '','SingleChoice','Unresolved','Architecture','No','Pattern',2"),
		patternDecisionInsert("'What are the names of the layers and their tasks?(Create sub decisions for each layer))', '','SingleChoice','Unresolved','Architecture','No','Pattern',2"),
		patternDecisionInsert("'What are the interfaces for each layer? (Create sub decisions)', '','SingleChoice','Unresolved','Architecture','No','Pattern',2"),
		patternDecisionInsert("'How do the layers structure together?', '','SingleChoice','Unresolved','Architecture','No','Pattern',2"),
		patternDecisionInsert("'How do the layers commnucate to each other?', '','SingleChoice','Unresolved','Architecture','No','Pattern',2"),
		patternDecisionInsert("'What is the error-handling strategy?', '','SingleChoice','Unresolved','Architecture','No','Pattern',2"),
		
		//Pipes and Filters
		patternDecisionInsert("'What is the sequence of processing tasks?', '','SingleChoice','Unresolved','Architecture','No','Pattern',3"),
		patternDecisionInsert("'What are the data formats to be passed along each pipe?', '','SingleChoice','Unresolved','Architecture','No','Pattern',3"),
		patternDecisionInsert("'How to implement the filters? (create sub decisions for each filter)', '','SingleChoice','Unresolved','Architecture','No','Pattern',3"),
		patternDecisionInsert("'How to implement each pipe connection?(create sub decisions for each connection)', '','SingleChoice','Unresolved','Architecture','No','Pattern',3"),
		patternDecisionInsert("'What are the data formats to be passed along each pipe?', '','SingleChoice','Unresolved','Architecture','No','Pattern',3"),
		patternDecisionInsert("'What is the error-handling strategy?', '','SingleChoice','Unresolved','Architecture','No','Pattern',3"),		
		
		//MVC 16
		patternDecisionInsert("'How to seperate human-computer interaction from core functionality?', '','SingleChoice','Unresolved','Architecture','No','Pattern',5"),
		patternDecisionInsert("'How to implement change-propagation mechanism?', 'Follow Publisher-Subscriber patter','SingleChoice','Unresolved','Architecture','No','Pattern',5"),
		patternDecisionInsert("'How to design and implement the views?', '','SingleChoice','Unresolved','Architecture','No','Pattern',5"),
		patternDecisionInsert("'How to design and implement the controllers?', '','SingleChoice','Unresolved','Architecture','No','Pattern',5"),
		patternDecisionInsert("'How to implement the view-controller relationship?', '','SingleChoice','Unresolved','Architecture','No','Pattern',5"),
		patternDecisionInsert("'How to implement the set-up of MVC?', '','SingleChoice','Unresolved','Architecture','No','Pattern',5"),
		patternDecisionInsert("'How to create view dynamically', '','SingleChoice','Unresolved','Architecture','No','Pattern',5"),
		patternDecisionInsert("'Pluggable controllers', '','SingleChoice','Unresolved','Architecture','No','Pattern',5"),
		patternDecisionInsert("'What is the infrastructure for hierarchical views and controllers?', '','SingleChoice','Unresolved','Architecture','No','Pattern',5"),
		
		//Presentation-Abstraction-Control
		patternDecisionInsert("'What is the model of the application?', '','SingleChoice','Unresolved','Architecture','No','Pattern',7"),
		patternDecisionInsert("'What is the general strategy for organizing the PAC hierarchy?', '','SingleChoice','Unresolved','Architecture','No','Pattern',7"),
		patternDecisionInsert("'What is the top-level PAC agent?', '','SingleChoice','Unresolved','Architecture','No','Pattern',7"),
		patternDecisionInsert("'What is the bottom-level PAC agents?', '','SingleChoice','Unresolved','Architecture','No','Pattern',7"),
		patternDecisionInsert("'What is the bottom-level PAC agents for system services?', '','SingleChoice','Unresolved','Architecture','No','Pattern',7"),
		patternDecisionInsert("'What is the intermediate-level PAC agents to compose lower-level PAC agents?', '','SingleChoice','Unresolved','Architecture','No','Pattern',7"),
		patternDecisionInsert("'What is the intermediate-level PAC agents to coordinate lower-level PAC agents?', '','SingleChoice','Unresolved','Architecture','No','Pattern',7"),
		patternDecisionInsert("'How to seperate core functionality from human-computer interaction?', '','SingleChoice','Unresolved','Architecture','No','Pattern',7"),
		};
		return toReturn;
		
	}
	
	/**
	 * Statements to insert pattern_ontentries relationship.
	 */
	private static String INSERT_PATTERN_ONTENTRIES[] =
	{
		//Three-Layer
		patternOntEntryInsert("1, 93, 'IS'"),//increase scalability
		patternOntEntryInsert("1, 100, 'IS'"),//reusability
		patternOntEntryInsert("1, 80, 'IS'"),//Increases Flexibility
		patternOntEntryInsert("1, 62, 'IS'"),//extensibility
		patternOntEntryInsert("1, 63, 'IS'"),		
		patternOntEntryInsert("1, 87, 'IS'"),
		//patternOntEntryInsert("1, 75, 'IS'"),
		patternOntEntryInsert("1, 83, 'IS'"),
		patternOntEntryInsert("1, 235, 'IS'"),
		patternOntEntryInsert("1, 274, 'IS'"),
		patternOntEntryInsert("1, 275, 'NOT'"),
		
		//Layers
		patternOntEntryInsert("2, 100, 'IS'"),//reusability
		patternOntEntryInsert("2, 80, 'IS'"),//Increases Flexibility
		patternOntEntryInsert("2, 62, 'IS'"), //extensibility
		patternOntEntryInsert("2, 63, 'IS'"),
		patternOntEntryInsert("2, 235, 'IS'"),
		patternOntEntryInsert("2, 87, 'IS'"), //portability	
		patternOntEntryInsert("2, 274, 'IS'"), //configurabiliity
		patternOntEntryInsert("2, 275, 'NOT'"), //performance criteria
		
		
		//Pipes and Filters
		patternOntEntryInsert("3, 80, 'IS'"),
		patternOntEntryInsert("3, 100, 'IS'"),
		patternOntEntryInsert("3, 62, 'IS'"),
		patternOntEntryInsert("3, 83, 'IS'"),
		patternOntEntryInsert("3, 21, 'IS'"),
		patternOntEntryInsert("3, 246, 'NOT'"),
		patternOntEntryInsert("3, 38, 'NOT'"),
		
		//Blackboard
		patternOntEntryInsert("4, 79, 'IS'"),
		patternOntEntryInsert("4, 83, 'IS'"),
		patternOntEntryInsert("4, 100, 'IS'"),
		patternOntEntryInsert("4, 136, 'IS'"),
		patternOntEntryInsert("4, 128, 'IS'"),
		patternOntEntryInsert("4, 226, 'NOT'"),
		patternOntEntryInsert("4, 3, 'NOT'"),
		patternOntEntryInsert("4, 275, 'NOT'"),
		
		//MVC
		patternOntEntryInsert("5, 83, 'IS'"), //adaptability
		patternOntEntryInsert("5, 62, 'IS'"), //extensibility		
		patternOntEntryInsert("5, 80, 'NOT'"), //Increases Flexibility
		
		//Broker
		patternOntEntryInsert("6, 87, 'IS'"), //portability
		patternOntEntryInsert("6, 79, 'IS'"), //modifiability
		patternOntEntryInsert("6, 62, 'IS'"), //extendibility
		patternOntEntryInsert("6, 101, 'IS'"), //interoperability
		patternOntEntryInsert("6, 100, 'IS'"), //reusability
		//THIS ONE HAS BEEN MODIFIED SINCE CONFIGURABILITY IS NO LONGER ON DEFAULT ONTENTRY.
		patternOntEntryInsert("6, 146, 'IS'"), //increases testability
		patternOntEntryInsert("6, 245, 'NOT'"), //performance criteria
		patternOntEntryInsert("6, 136, 'NOT'"), //fault tolerance
		
		//Presentation-Abstraction-Control
		patternOntEntryInsert("7, 92, 'IS'"), //scalability
		patternOntEntryInsert("7, 79, 'IS'"), //modifiability
		patternOntEntryInsert("7, 62, 'IS'"), //extendibility
		patternOntEntryInsert("7, 253, 'IS'"), //{provides | supports} multi-threading
		patternOntEntryInsert("7, 235, 'NOT'"), //maintainability criteria
		patternOntEntryInsert("7, 245, 'NOT'"), //performance criteria
		
		//Microkernel
		patternOntEntryInsert("8, 87, 'IS'"), //portability
		patternOntEntryInsert("8, 80, 'IS'"), //Increases Flexibility
		patternOntEntryInsert("8, 62, 'IS'"), //extendibility
		patternOntEntryInsert("8, 92, 'IS'"), //scalability
		patternOntEntryInsert("8, 152, 'IS'"), //reliability
		patternOntEntryInsert("8, 245, 'NOT'"), //performance criteri
		patternOntEntryInsert("8, 3, 'NOT'"), //development cost
		
		//Reflection
		patternOntEntryInsert("9, 79, 'IS'"), //modifiability
		patternOntEntryInsert("9, 62, 'IS'"), ////extendibility
		patternOntEntryInsert("9, 245, 'NOT'"), //performance criteria
		patternOntEntryInsert("9, 243, 'NOT'"), //supportability
		
		//Design Patterns
		//Whole-Part
		patternOntEntryInsert("10, 79, 'IS'"), //modifiability
		patternOntEntryInsert("10, 100, 'IS'"), //reusability
		patternOntEntryInsert("10, 245, 'NOT'"), //performance criteria
		
		//Master-Slave
		patternOntEntryInsert("11, 245, 'IS'"), //performance criteria
		patternOntEntryInsert("11, 62, 'IS'"), //extendibility
		patternOntEntryInsert("11, 3, 'NOT'"), //development cost
		patternOntEntryInsert("11, 87, 'NOT'"), //portability
		
		//Proxy
		patternOntEntryInsert("12, 275, 'IS'"), //efficiency
		patternOntEntryInsert("12, 277, 'IS'"), 
		patternOntEntryInsert("12, 246, 'NOT'"), //throughput
		
		//Command Processor
		patternOntEntryInsert("13, 80, 'IS'"), //increase flexibility
		patternOntEntryInsert("13, 213, 'IS'"), //verificability
		patternOntEntryInsert("13, 275, 'NOT'"), //efficiency
		
		//View handler
		patternOntEntryInsert("14, 62, 'IS'"), //extensibility
		patternOntEntryInsert("14, 79, 'IS'"), //modifiability
		patternOntEntryInsert("14, 275, 'NOT'"), //efficiency
		
		//Forwarder-Receiver
		patternOntEntryInsert("15, 275, 'IS'"), //efficiency
		patternOntEntryInsert("15, 274, 'NOT'"), //configuration
		
		//Client-Dispatcher-Server
		patternOntEntryInsert("16, 274, 'IS'"), //configuration
		patternOntEntryInsert("16, 87, 'IS'"), //Portability
		patternOntEntryInsert("16, 83, 'IS'"), //Adaptability
		patternOntEntryInsert("16, 136, 'IS'"), //fault tolerance
		patternOntEntryInsert("16, 275, 'NOT'"), //efficiency
		patternOntEntryInsert("16, 79, 'NOT'"), //modifiability
		
		//Transaction Script
		patternOntEntryInsert("26, 3, 'IS'"), //development cost
		patternOntEntryInsert("26, 241, 'IS'"), //readability
		patternOntEntryInsert("26, 79, 'NOT'"), //modifiability
		
		//Domain Model
		patternOntEntryInsert("27, 83, 'IS'"), //adaptability
		patternOntEntryInsert("27, 80, 'IS'"), //increase flexibility
		
		//Table Module
		
	};
	
	/**
	 * Statements to insert pattern_decision relationships
	 */
	private static String INSERT_PATTERN_DECISION_RELATIONSHIPS[] =
	{
		patternDecisionRelationshipInsert("26, 1, 'Decision'"),
		patternDecisionRelationshipInsert("27, 1, 'Decision'"),
		patternDecisionRelationshipInsert("28, 1, 'Decision'"),
		//patternDecisionRelationshipInsert("29, 2, 'Decision'"),
		patternDecisionRelationshipInsert("30, 2, 'Decision'"),
		patternDecisionRelationshipInsert("31, 2, 'Decision'"),
		patternDecisionRelationshipInsert("32, 2, 'Decision'"),
		patternDecisionRelationshipInsert("37, 2, 'Decision'"),
		patternDecisionRelationshipInsert("33, 3, 'Decision'"),
		patternDecisionRelationshipInsert("34, 3, 'Decision'"),
		patternDecisionRelationshipInsert("35, 3, 'Decision'"),
		patternDecisionRelationshipInsert("36, 3, 'Decision'"),
		
		patternDecisionRelationshipInsert("25, 6, 'Decision'"),
		patternDecisionRelationshipInsert("18, 7, 'Decision'"),
		patternDecisionRelationshipInsert("37, 7, 'Decision'"),
		
		patternDecisionRelationshipInsert("17, 17, 'Decision'"),
		patternDecisionRelationshipInsert("19, 20, 'Decision'"),
		patternDecisionRelationshipInsert("14, 22, 'Decision'"),
		patternDecisionRelationshipInsert("21, 24, 'Decision'"),
		patternDecisionRelationshipInsert("24, 24, 'Decision'"),
	};
	
	/**
	 * Statements to insert problem categories entity
	 */
	private static String[] INSERT_PATTERNPROBLEMCATEGORIES()
	{	
		String[] toReturn = {
		patternProblemCategoriesInsert("'From_Mud_To_Structure', 'Architecture'"),
		patternProblemCategoriesInsert("'Distributed_Systems', 'Architecture'"),
		patternProblemCategoriesInsert("'Interactive_Systems', 'Architecture'"),
		patternProblemCategoriesInsert("'Adaptable_Systems', 'Architecture'"),
		patternProblemCategoriesInsert("'Structural_Decomposition', 'Design'"),
		patternProblemCategoriesInsert("'Organization_Of_Work', 'Design'"),
		patternProblemCategoriesInsert("'Access_Control', 'Design'"),
		patternProblemCategoriesInsert("'Management', 'Design'"),
		patternProblemCategoriesInsert("'Communication', 'Design'"),
		patternProblemCategoriesInsert("'Other_Architecture_Problem', 'Architecture'"),
		patternProblemCategoriesInsert("'Other_Design_Problem', 'Design'") };
		return toReturn;
	}
	
	/**
	 * Statements to insert pattern and problem category relationships
	 */
	private static String INSERT_PATTERN_PROBLEMCATEGORY_RELATIONSHIPS[] =
	{		
		pattern_problemCategoryInsert("1, 1"),
		pattern_problemCategoryInsert("2, 1"),
		pattern_problemCategoryInsert("3, 1"),
		pattern_problemCategoryInsert("4, 1"),
		pattern_problemCategoryInsert("6, 2"),
		pattern_problemCategoryInsert("5, 3"),
		pattern_problemCategoryInsert("7, 3"),
		pattern_problemCategoryInsert("8, 4"),
		pattern_problemCategoryInsert("9, 4"),
		pattern_problemCategoryInsert("10, 5"),
		pattern_problemCategoryInsert("11, 6"),
		pattern_problemCategoryInsert("12, 7"),
		pattern_problemCategoryInsert("13, 8"),
		pattern_problemCategoryInsert("14, 8"),
		pattern_problemCategoryInsert("15, 9"),
		pattern_problemCategoryInsert("16, 9"),
		pattern_problemCategoryInsert("17, 9"),
		pattern_problemCategoryInsert("18, 11"),
		pattern_problemCategoryInsert("19, 11"),
		pattern_problemCategoryInsert("20, 11"),
		pattern_problemCategoryInsert("21, 11"),
		pattern_problemCategoryInsert("22, 11"),
		pattern_problemCategoryInsert("23, 11"),
		pattern_problemCategoryInsert("24, 11"),
		pattern_problemCategoryInsert("25, 11"),
		pattern_problemCategoryInsert("26, 11"),
		pattern_problemCategoryInsert("27, 11"),
		pattern_problemCategoryInsert("28, 11"),
		pattern_problemCategoryInsert("29, 11"),
		pattern_problemCategoryInsert("30, 11"),
		pattern_problemCategoryInsert("31, 11"),
		pattern_problemCategoryInsert("32, 11"),
		pattern_problemCategoryInsert("33, 11"),
		pattern_problemCategoryInsert("34, 11"),
		pattern_problemCategoryInsert("35, 11"),
		pattern_problemCategoryInsert("36, 11")
	};
	

	public static final String CREATE_PATTERNS(){
		return beginTable("patterns")
		+ tablePart("id INTEGER NOT NULL ") //pay attention to the space after "NULL"
		+ tablePart("name varchar(255) default NULL")
		//+ tablePart("type ENUM('architecture','design','idiom') default 'design'")
		+ tablePart("type varchar(255) default NULL")
		+ tablePart("description blob default NULL")
		+ tablePart("problem blob default NULL")
		+ tablePart("context blob default NULL")
		+ tablePart("solution blob default NULL")
		+ tablePart("implementation blob default NULL")
		+ tablePart("example blob default NULL")
		+ tablePart("url varchar(255) default NULL")
		+ endTable("PRIMARY KEY (id)");
	}
	
	/**
	 * Returns the decisions table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_PATTERNDECISIONS() {
		return beginTable("patterndecisions")
		+ tablePart("id INTEGER NOT NULL ")
		+ tablePart("name varchar(255) default NULL")
		+ tablePart("description varchar(255) default NULL")
		+ tablePart("type varchar(255) default NULL")
		+ tablePart("status varchar(255) default NULL")
		+ tablePart("phase varchar(255) default NULL")
		+ tablePart("subdecreq varchar(255) default NULL")
		+ tablePart("ptype varchar(255) default NULL")
		+ tablePart("parent INTEGER  default NULL")
		+ tablePart("subsys INTEGER  default NULL")
		+ tablePart("designer INTEGER  default NULL")
		+ tablePart("parentpattern int")
		+ endTable("PRIMARY KEY  (id)");
	}
	
	/**
	 * Returns the pattern_decision table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_PATTERN_DECISION() {
		return beginTable("pattern_decision")
		+ tablePart("patternID INTEGER default NULL")
		+ tablePart("decisionID INTEGER default NULL")
		+ endTable("parentType varchar(255) default NULL");
	}
	
	/**
	 * Returns the pattern problem categories table statement
	 * @return
	 */
	public static final String CREATE_PATTERNPROBLEMCATEGORIES() {
		return beginTable("patternproblemcategories")
		+ tablePart("id INTEGER NOT NULL ")
		+ tablePart("problemcategory varchar(50) default NULL")
		+ tablePart("patterntype varchar(20) default NULL")
		+ endTable("PRIMARY KEY  (id)");
	}
	
	/**
	 * Return the pattern and problem category relationship table statement
	 * @return
	 */
	public static final String CREATE_PATTERN_PROBLEMCATEGORY_RELATIONSHIP() {
		return beginTable("pattern_problemcategory")
		+ tablePart("id INTEGER NOT NULL " + autoIncrement())
		+ tablePart("patternID INTEGER default NULL")
		+ tablePart("problemcategoryID INTEGER default NULL")
		+ endTable("PRIMARY KEY  (id)");
	}
	
	/**
	 * Returns the pattern_ontentries table statement.
	 * @return String containing the statement
	 */
	public static final String CREATE_PATTERN_ONTENTRIES() {
		return beginTable("pattern_ontentries")
		+ tablePart("patternID INTEGER default NULL")
		+ tablePart("ontID INTEGER default NULL")
		+ endTable("direction varchar(50) default NULL");
	}
	public static String[] getPatternQueries(){
		return INSERT_PATTERNS();
	}
	
	public static String[] getPatternDecisionQueries(){
		return INSERT_PATTERN_DECISIONS();
	}
	
	public static String[] getPatternOntEntryQueries(){
		return INSERT_PATTERN_ONTENTRIES;
	}
	
	public static String[] getPatternDecisionRelationShipQueries(){
		return INSERT_PATTERN_DECISION_RELATIONSHIPS;
	}
	
	public static String[] getPatternProblemCategoriesQueries(){
		return INSERT_PATTERNPROBLEMCATEGORIES();
	}
	
	/**
	 * This one gets the insert statement for relationship between pattern and category
	 * @return
	 */
	public static String[] getPatternProblemCategoryQueries(){
		return INSERT_PATTERN_PROBLEMCATEGORY_RELATIONSHIPS;
	}
	
}
