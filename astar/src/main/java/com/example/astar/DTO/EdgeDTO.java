package com.example.astar.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class EdgeDTO {
    private int id;
    private String path;
    private int start;
    private int end;
    private String type;
    private int weigh;
    private int distance;
}
