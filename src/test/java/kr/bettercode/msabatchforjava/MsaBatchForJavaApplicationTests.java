package kr.bettercode.msabatchforjava;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBatchTest
@SpringBootTest
class MsaBatchForJavaApplicationTests {

  @Autowired
  JobLauncherTestUtils jobLauncherTestUtils;

  @Autowired
  JdbcTemplate jdbcTemplate;

  @Test
  @DisplayName("Batch Job이 실행되고, 해당 Job이 완료되면 Job과 Step은 COMPLETED 상태가 된다.")
  void jobExecutionTest() throws Exception {
    // given
    final Integer beforeJobCount = getExampleSummaryCount();

    // when
    final JobExecution jobExecution = jobLauncherTestUtils.launchJob();
    final Integer afterJobCount = getExampleSummaryCount();

    // then
    assertAll(
        () -> assertThat(jobExecution.getExitStatus().getExitCode()).isEqualTo("COMPLETED"),
        () -> assertThat(jobExecution.getStepExecutions())
            .extracting(stepExecution -> stepExecution.getExitStatus().getExitCode())
            .hasSize(2)
            .containsExactly("COMPLETED", "COMPLETED"),
        () -> assertThat(beforeJobCount).isEqualTo(0),
        () -> assertThat(afterJobCount).isEqualTo(6)
    );
  }

  private Integer getExampleSummaryCount() {
    return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM example_summary", Integer.class);
  }
}
