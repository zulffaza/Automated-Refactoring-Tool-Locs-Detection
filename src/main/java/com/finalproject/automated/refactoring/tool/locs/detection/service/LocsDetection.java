package com.finalproject.automated.refactoring.tool.locs.detection.service;

import lombok.NonNull;

/**
 * @author fazazulfikapp
 * @version 1.0.0
 * @since 24 October 2018
 */

public interface LocsDetection {

    Long llocDetection(@NonNull String body);
}
