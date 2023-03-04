package com.songlian.logistics.calculate;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TspData {
    double minLength;
    String path;
}
