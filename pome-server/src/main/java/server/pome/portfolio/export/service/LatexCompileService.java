package server.pome.portfolio.export.service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import server.pome.global.exception.BaseException;
import server.pome.global.exception.BaseResponseStatus;
import server.pome.portfolio.export.latex.filedata.LatexDocument;
import server.pome.portfolio.export.latex.filedata.PdfDocument;

@Slf4j
@Service
public class LatexCompileService {

  private final String latexCommand;
  private final Duration timeout;
  private final boolean enableInstaller;

  public LatexCompileService(
      @Value("${app.latex.command:xelatex}") String latexCommand,
      @Value("${app.latex.timeout-seconds:20}") long timeoutSeconds,
      @Value("${app.latex.enable-installer:true}") boolean enableInstaller
  ) {
    this.latexCommand = latexCommand;
    this.timeout = Duration.ofSeconds(timeoutSeconds);
    this.enableInstaller = enableInstaller;
  }

  public PdfDocument compile(LatexDocument document) {
    Path workDir = null;

    try {
      workDir = Files.createTempDirectory("pome-latex-");
      Path texFile = workDir.resolve(document.fileName());
      Path processLogFile = workDir.resolve(replaceExtension(document.fileName(), "process.log"));

      Files.writeString(texFile, document.source(), StandardCharsets.UTF_8);

      List<String> command = buildCommand(texFile.getFileName().toString());

      ProcessBuilder processBuilder = new ProcessBuilder(command).directory(workDir.toFile());
      processBuilder.redirectErrorStream(true);
      processBuilder.redirectOutput(processLogFile.toFile());

      Process process = processBuilder.start();
      boolean completed = process.waitFor(timeout.toSeconds(), TimeUnit.SECONDS);

      if (!completed) {
        process.destroyForcibly();
        log.warn("LaTeX compile timed out. command={}, timeoutSeconds={}, log={}",
            command, timeout.toSeconds(), readTail(processLogFile));
        throw new BaseException(BaseResponseStatus.SERVER_ERROR);
      }

      if (process.exitValue() != 0) {
        log.warn("LaTeX compile failed. command={}, exitCode={}, log={}",
            command, process.exitValue(), readTail(processLogFile));
        throw new BaseException(BaseResponseStatus.SERVER_ERROR);
      }

      Path pdfFile = workDir.resolve(replaceExtension(document.fileName(), "pdf"));
      if (!Files.exists(pdfFile)) {
        log.warn("LaTeX compile completed without PDF. command={}, log={}",
            command, readTail(processLogFile));
        throw new BaseException(BaseResponseStatus.SERVER_ERROR);
      }

      byte[] pdfContent = Files.readAllBytes(pdfFile);
      return new PdfDocument(pdfFile.getFileName().toString(), pdfContent);
    } catch (IOException e) {
      log.warn("Failed to run LaTeX compiler. command={}, message={}",
          latexCommand, e.getMessage(), e);
      throw new BaseException(BaseResponseStatus.SERVER_ERROR);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      log.warn("LaTeX compile interrupted. command={}", latexCommand, e);
      throw new BaseException(BaseResponseStatus.SERVER_ERROR);
    } finally {
      deleteQuietly(workDir);
    }
  }

  private String replaceExtension(String fileName, String extension) {
    int dotIndex = fileName.lastIndexOf('.');
    String baseName = dotIndex >= 0 ? fileName.substring(0, dotIndex) : fileName;
    return baseName + "." + extension;
  }

  private List<String> buildCommand(String texFileName) {
    List<String> command = new ArrayList<>();
    command.add(latexCommand);
    if (enableInstaller) {
      command.add("--enable-installer");
    }
    command.add("-interaction=nonstopmode");
    command.add("-halt-on-error");
    command.add(texFileName);
    return command;
  }

  private String readTail(Path logFile) {
    if (logFile == null || !Files.exists(logFile)) {
      return "";
    }

    try {
      List<String> lines = Files.readAllLines(logFile, StandardCharsets.UTF_8);
      int fromIndex = Math.max(0, lines.size() - 40);
      return String.join("\n", lines.subList(fromIndex, lines.size()));
    } catch (IOException e) {
      return "Failed to read xelatex log: " + e.getMessage();
    }
  }

  private void deleteQuietly(Path directory) {
    if (directory == null || !Files.exists(directory)) {
      return;
    }

    try (var paths = Files.walk(directory)) {
      paths.sorted(Comparator.reverseOrder())
          .forEach(path -> {
            try {
              Files.deleteIfExists(path);
            } catch (IOException ignored) {
              // 임시 파일 정리에 실패해도 PDF 생성 응답 자체에는 영향을 주지 않는다.
            }
          });
    } catch (IOException ignored) {
      // 임시 디렉터리 순회 실패도 무시한다.
    }
  }
}
