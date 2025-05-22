package com.university.educationPackage.avatars;

import com.university.educationPackage.models.User;
import com.university.educationPackage.models.Program;
import org.springframework.stereotype.Service;

@Service
public class AvatarService {

    /**
     * Obtiene la configuración de avatar para un usuario según su programa
     */
    public String getAvatarConfig(User user) {
        Program program = user.getStudentRole().getProgram();
        return switch(program.getName().toLowerCase()) {
            case "medicina" -> buildMedicalAvatar();
            case "derecho" -> buildLawAvatar();
            case "ingeniería de software" -> buildSoftwareEngineerAvatar();
            default -> buildDefaultAvatar();
        };
    }

    private String buildMedicalAvatar() {
        return """
            {
                "avatarType": "professional",
                "appearance": "white-coat",
                "voice": "calm",
                "animations": ["explaining", "pointing"]
            }""";
    }

    private String buildLawAvatar() {
        return """
            {
                "avatarType": "formal",
                "appearance": "suit",
                "voice": "authoritative",
                "animations": ["arguing", "document-review"]
            }""";
    }

    private String buildSoftwareEngineerAvatar() {
        return """
            {
                "avatarType": "casual",
                "appearance": "hoodie",
                "voice": "enthusiastic",
                "animations": ["coding", "presenting"]
            }""";
    }

    private String buildDefaultAvatar() {
        return """
            {
                "avatarType": "generic",
                "appearance": "neutral",
                "voice": "neutral",
                "animations": ["greeting"]
            }""";
    }
}
