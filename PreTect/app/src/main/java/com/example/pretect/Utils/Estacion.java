package com.example.pretect.Utils;

public class Estacion {

    String departamento,
            ciudad_municipio,
            unidad,
            tipo_unidad,
            codigo_cuadrante,
            cuadrante,
            numero_celular_cuadrante;

    public Estacion(String departamento, String ciudad_municipio, String unidad, String tipo_unidad, String codigo_cuadrante, String cuadrante, String numero_celular_cuadrante) {
        this.departamento = departamento;
        this.ciudad_municipio = ciudad_municipio;
        this.unidad = unidad;
        this.tipo_unidad = tipo_unidad;
        this.codigo_cuadrante = codigo_cuadrante;
        this.cuadrante = cuadrante;
        this.numero_celular_cuadrante = numero_celular_cuadrante;
    }

    public Estacion(String ciudad_municipio, String tipo_unidad, String numero_celular_cuadrante) {
        this.ciudad_municipio = ciudad_municipio;
        this.tipo_unidad = tipo_unidad;
        this.numero_celular_cuadrante = numero_celular_cuadrante;
    }

    public Estacion() {
    }

    public String getCiudad_municipio() {
        return ciudad_municipio;
    }

    public void setCiudad_municipio(String ciudad_municipio) {
        this.ciudad_municipio = ciudad_municipio;
    }

    public String getTipo_unidad() {
        return tipo_unidad;
    }

    public void setTipo_unidad(String tipo_unidad) {
        this.tipo_unidad = tipo_unidad;
    }

    public String getNumero_celular_cuadrante() {
        return numero_celular_cuadrante;
    }

    public void setNumero_celular_cuadrante(String numero_celular_cuadrante) {
        this.numero_celular_cuadrante = numero_celular_cuadrante;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }

    public String getCodigo_cuadrante() {
        return codigo_cuadrante;
    }

    public void setCodigo_cuadrante(String codigo_cuadrante) {
        this.codigo_cuadrante = codigo_cuadrante;
    }

    public String getCuadrante() {
        return cuadrante;
    }

    public void setCuadrante(String cuadrante) {
        this.cuadrante = cuadrante;
    }
}
