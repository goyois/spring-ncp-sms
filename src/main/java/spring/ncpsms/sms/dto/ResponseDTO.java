package spring.ncpsms.sms.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
@Builder
public class ResponseDTO {
    private String requestId;
    private LocalDateTime reqyestTime;
    private String statusCode;
    private String statusName;
    private String smsConfirmNum;


    public ResponseDTO(String smsConfirmNum) {
        this.smsConfirmNum = smsConfirmNum;
    }
}
