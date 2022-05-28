package com.example.astar.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NodeDTO {
    private int id;
    private double lat;
    private double lon;
    private String name;
}
