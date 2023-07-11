package spring.ncpsms.sms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class RequestDTO {

    private String type;
    private String contentType;
    private String countryType;
    private String from;
    private String content;
    private List<MessageDTO> messages;
}
