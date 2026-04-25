package com.sliit.facilitiescatalogue.auth;

import java.util.List;

public record AuthUserResponse(
        String username,
        List<String> roles
) {
}

