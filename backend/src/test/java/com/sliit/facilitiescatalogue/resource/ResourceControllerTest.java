package com.sliit.facilitiescatalogue.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sliit.facilitiescatalogue.resource.dto.ResourceRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ResourceController.class)
@AutoConfigureMockMvc
@Import(com.sliit.facilitiescatalogue.config.SecurityConfig.class)
class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ResourceService resourceService;

    @Test
    @WithMockUser(roles = "USER")
    void shouldReturnResourcesForAuthorizedUser() throws Exception {
        ResourceEntity entity = new ResourceEntity();
        entity.setId(1L);
        entity.setResourceCode("LH-001");
        entity.setName("Main Hall");
        entity.setType(ResourceType.LECTURE_HALL);
        entity.setCapacity(100);
        entity.setLocation("Malabe");
        entity.setAvailableFrom(LocalTime.of(8, 0));
        entity.setAvailableTo(LocalTime.of(18, 0));
        entity.setStatus(ResourceStatus.ACTIVE);
        entity.setActive(true);

        when(resourceService.getAllResources(null, null, null, null, null, null))
                .thenReturn(List.of(new ResourceMapper().toResponse(entity)));

        mockMvc.perform(get("/api/resources"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resourceCode").value("LH-001"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldRejectCreateForNonAdmin() throws Exception {
        mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buildRequest())))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateForAdmin() throws Exception {
        ResourceRequest request = buildRequest();

        ResourceEntity entity = new ResourceEntity();
        entity.setId(1L);
        entity.setResourceCode(request.resourceCode());
        entity.setName(request.name());
        entity.setType(request.type());
        entity.setCapacity(request.capacity());
        entity.setLocation(request.location());
        entity.setDescription(request.description());
        entity.setAvailableFrom(request.availableFrom());
        entity.setAvailableTo(request.availableTo());
        entity.setStatus(request.status());
        entity.setActive(request.active());

        when(resourceService.createResource(any(ResourceRequest.class)))
                .thenReturn(new ResourceMapper().toResponse(entity));

        mockMvc.perform(post("/api/resources")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.resourceCode").value("MR-102"));
    }

    private ResourceRequest buildRequest() {
        return new ResourceRequest(
                "MR-102",
                "Meeting Room 102",
                ResourceType.MEETING_ROOM,
                12,
                "Metro Campus",
                "Board room",
                LocalTime.of(8, 0),
                LocalTime.of(18, 0),
                ResourceStatus.ACTIVE,
                true
        ) ;
    }
}

