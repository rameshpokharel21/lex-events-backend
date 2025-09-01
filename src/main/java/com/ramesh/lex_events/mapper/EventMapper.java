package com.ramesh.lex_events.mapper;

import com.ramesh.lex_events.dto.request.EventRequest;
import com.ramesh.lex_events.dto.response.EventResponse;
import com.ramesh.lex_events.models.Event;
import com.ramesh.lex_events.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EventMapper {
    Event toEntity(EventRequest eventRequest);

    @Mapping(target = "creator", expression = "java(mapUser(event))")
    EventResponse toResponse(Event event);

    default EventResponse.SimpleUser mapUser(Event event){
        User user = event.getCreatedBy();
        EventResponse.SimpleUser simple = new EventResponse.SimpleUser();
        simple.setUserId(user.getUserId());
        simple.setUserName(user.getUserName());
        if(Boolean.TRUE.equals(event.getShowContactInfo())){
            simple.setEmail(user.getEmail());
            simple.setPhoneNumber(user.getPhoneNumber());
        }
        return simple;
    }
}
