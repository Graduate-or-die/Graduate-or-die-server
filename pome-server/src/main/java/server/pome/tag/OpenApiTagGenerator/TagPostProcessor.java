package server.pome.tag.OpenApiTagGenerator;

import java.util.List;

public interface TagPostProcessor {

  List<String> process(List<String> raw);

}
