package com.transandina.sigepro.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "recurso_tarea")
public class RecursoTarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_recurso_tarea")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tarea", nullable = false)
    private Tarea tarea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "horas_estimadas", precision = 6, scale = 2)
    private BigDecimal horasEstimadas = BigDecimal.ZERO;

    @Column(name = "horas_reales", precision = 6, scale = 2)
    private BigDecimal horasReales = BigDecimal.ZERO;

    public RecursoTarea() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Tarea getTarea() { return tarea; }
    public void setTarea(Tarea tarea) { this.tarea = tarea; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public BigDecimal getHorasEstimadas() { return horasEstimadas; }
    public void setHorasEstimadas(BigDecimal horasEstimadas) { this.horasEstimadas = horasEstimadas; }
    public BigDecimal getHorasReales() { return horasReales; }
    public void setHorasReales(BigDecimal horasReales) { this.horasReales = horasReales; }
}
