package zingg.documenter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import zingg.ZinggSparkTester;


public class TestDocumenterBase extends ZinggSparkTester {
	public static final Log LOG = LogFactory.getLog(TestDocumenterBase.class);
	private final String TEST_DOC_TEMPLATE = "testDocumenterTemplate.ftlh";

	@DisplayName ("Test Column is a Z column or not")
	@Test
	public void testIfColumnIsZColumn() throws Throwable {
		DocumenterBase base = new DocumenterBase(spark, args);
		String aZColumn = "z_sampleColumn";
		
		assertTrue(base.isZColumn(aZColumn), "Column is not a Z column");
		String aNonZColumn = "sampleColumn";
		assertFalse(base.isZColumn(aNonZColumn), "Column is a Z column");

	}
	
	@DisplayName ("Test if a directory already exists else it is created")
	@Test
	public void testIfDirectoryAlreadyExistsElseCreate() throws Throwable {
		
		DocumenterBase base = new DocumenterBase(spark, args);
		Method f = DocumenterBase.class.getDeclaredMethod("checkAndCreateDir", String.class);
		f.setAccessible(true);

		f.invoke(base, args.getZinggDir());
		assertTrue(Files.exists(Paths.get(args.getZinggDir())), "The directory doesn't exist");
		f.invoke(base, "/an/invalid/dir");
		assertFalse(Files.exists(Paths.get("/a/invalid/dir")), "The directory does exist");
	}

	@DisplayName ("Test process Template to make document")
	@Test
	public void testProcessTemplateToMakeDocument() throws Throwable {
		
		DocumenterBase base = new DocumenterBase(spark, args);
		Method f = DocumenterBase.class.getDeclaredMethod("writeDocument", String.class, Map.class, String.class);
		f.setAccessible(true);

		Map<String, Object> root = new HashMap<String, Object>();
		root.put(TemplateFields.TITLE, "template test");
		root.put(TemplateFields.MODEL_ID, "100");
		List<String> aList = Arrays.asList("welcome", "to", "zingg");
		root.put(TemplateFields.CLUSTERS, aList);
		root.put(TemplateFields.NUM_COLUMNS, 2);
		root.put(TemplateFields.FIELD_DEFINITION_COUNT, 2); 
		root.put(TemplateFields.ISMATCH_COLUMN_INDEX, 0);
		root.put(TemplateFields.CLUSTER_COLUMN_INDEX, 1);; 
		String fileName = args.getZinggDir() + "/testDoc.html";
		f.invoke(base, TEST_DOC_TEMPLATE, root, fileName);
		
		String content = Files.readString(Paths.get(fileName));
		assertTrue(content.contains("100"));
		assertTrue(content.contains("welcome"));
		assertTrue(content.contains("zingg"));
		assertTrue(content.contains("template test"));
	}
}
