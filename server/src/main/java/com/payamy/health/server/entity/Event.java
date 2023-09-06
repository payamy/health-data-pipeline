package com.payamy.health.server.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;
import com.vladmihalcea.hibernate.type.json.JsonType;
import org.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

//@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//@TypeDefs({
//        @TypeDef(name = "json", typeClass = JsonType.class)
//})
//@Table(name="events")
public class Event implements Serializable {

//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long eventId;
    private String eventName;

    private Long userId;

//    @Type(type = "json")
//    @Column(name = "payload", columnDefinition = "json")
    private String payload;
    private Date sentAt;
}
