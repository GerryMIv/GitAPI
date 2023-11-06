import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CreateRepoForAuthUserTests.ValidRequests.class,
        CreateRepoForAuthUserTests.InvalidRequests.class
})
public class Runner {
}
