package server.pome.portfolio.export.latex.filedata;

public record PdfDocument(
    String fileName,
    byte[] content
) {}
