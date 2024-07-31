package com.mads.ai.langchain4j.model;

import java.time.LocalDate;

public class PersonModel {
    private String firstName;
    private String lastName;
    private LocalDate birthDate;

    @Override
    public String toString() {
        return "Person {" +
                " firstName = \"" + firstName + "\"" +
                ", lastName = \"" + lastName + "\"" +
                ", birthDate = " + birthDate +
                " }";
    }
}
