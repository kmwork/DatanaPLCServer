package ru.datana.steel.plc.db;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SimpleEntity {
    @Id
    private String id;
}
