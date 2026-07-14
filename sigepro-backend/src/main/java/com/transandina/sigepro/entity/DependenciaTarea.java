package com.transandina.sigepro.entity;

import com.transandina.sigepro.enums.TipoDependencia;
import jakarta.persistence.*;

@Entity
@Table(name = "dependencia_tarea")
public class DependenciaTarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dependencia")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tarea_origen", nullable = false)
    private Tarea tareaOrigen;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tarea_destino", nullable = false)
    private Tarea tareaDestino;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDependencia tipo = TipoDependencia.FIN_INICIO;

    public DependenciaTarea() {}

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Tarea getTareaOrigen() { return tareaOrigen; }
    public void setTareaOrigen(Tarea tareaOrigen) { this.tareaOrigen = tareaOrigen; }
    public Tarea getTareaDestino() { return tareaDestino; }
    public void setTareaDestino(Tarea tareaDestino) { this.tareaDestino = tareaDestino; }
    public TipoDependencia getTipo() { return tipo; }
    public void setTipo(TipoDependencia tipo) { this.tipo = tipo; }
}
