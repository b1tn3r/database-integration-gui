package Database_Integration.Test_Suites;

// Test suite to specifically only call for integration tests only

import Database_Integration.InsertFormController_IntegrationTest;
import Database_Integration.IntegrationTest;
import org.junit.experimental.categories.Categories;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Categories.class)
@Categories.IncludeCategory(IntegrationTest.class)
@Suite.SuiteClasses(InsertFormController_IntegrationTest.class)      // does not need to be added
public class IntegrationTestSuite {

}
