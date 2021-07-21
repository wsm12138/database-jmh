package icu.wwj.jmh.shardingsphere5;

import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import javax.sql.DataSource;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Random;

@State(Scope.Group)
@Fork(1)
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 10, time = 1)
public class ReadwriteSplittingBenchmark {
    
    private final Random random = new Random();
    
    private DataSource dataSource;
    
    private Connection connection;
    
    private PreparedStatement preparedStatement;
    
    public ReadwriteSplittingBenchmark() {
        try {
            dataSource = YamlShardingSphereDataSourceFactory.createDataSource(new File(getClass().getResource("/shardingsphere-jdbc/readwrite-splitting.yaml").getFile()));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    @Setup(Level.Iteration)
    public void setup() throws Exception {
        connection = dataSource.getConnection();
        preparedStatement = connection.prepareStatement("select c from sbtest1 where id = ?");
    }
    
    @Group
    @Benchmark
    public void benchReadwriteSplitting() throws Exception {
        preparedStatement.setInt(1, random.nextInt(100000));
        preparedStatement.execute();
    }
    
    @TearDown(Level.Iteration)
    public void tearDown() throws Exception {
        preparedStatement.close();
        connection.close();
    }
}
