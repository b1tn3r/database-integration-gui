package Database_Integration.Test_Suites;


import Database_Integration.InsertFormController_UnitTest;
import Database_Integration.IntegrationTest;
import org.junit.experimental.categories.Categories;
import org.junit.experimental.categories.Categories.ExcludeCategory;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Categories.class)
@ExcludeCategory(IntegrationTest.class)
@SuiteClasses(InsertFormController_UnitTest.class)     // does not need to be added.. only used for EXTRA tests outside of Unit tests
public class UnitTestSuite {

}
