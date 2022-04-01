package com.example.SoftbinatorProject.models;

public enum ReceiptDetails {
    NR,
    DATA,
    NUME,
    NUME_ORGANIZATIE,
    NUME_PROIECT,
    ;

    @Override
    public String toString() {
        return this.name().replace('_', ' ');
    }
}
