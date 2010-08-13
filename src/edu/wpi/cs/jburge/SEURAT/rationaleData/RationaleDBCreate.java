package edu.wpi.cs.jburge.SEURAT.rationaleData;

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
		+ tablePart("id INTEGER NOT NULL " + autoIncrement())
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
			CREATE_XFEATUREMAPPING()
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
}
