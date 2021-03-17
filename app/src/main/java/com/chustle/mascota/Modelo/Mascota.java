package com.chustle.mascota.Modelo;

public class Mascota {
    public int id = -1;
    public String nombre;
    public int especie = -1;
    public int raza = -1;
    public long nacimiento = -1;
    public String usuario;

    public Mascota() {
    }

    public Mascota(int id, String nombre, int especie, int raza, long nacimiento, String usuario) {
        this.id = id;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.nacimiento = nacimiento;
        this.usuario = usuario;
    }
}
