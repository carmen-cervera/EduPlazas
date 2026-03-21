package com.eduplazas.backend.controller;

import com.eduplazas.backend.model.Convocatoria;
import com.eduplazas.backend.model.Oferta;
import com.eduplazas.backend.model.Solicitante;
import com.eduplazas.backend.model.Solicitud;
import com.eduplazas.backend.service.SolicitudService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/solicitudes")
@CrossOrigin(origins = "http://localhost:5173")
public class SolicitudController {

    private final SolicitudService solicitudService;

    public SolicitudController(SolicitudService solicitudService) {
        this.solicitudService = solicitudService;
    }
    
    // ver el solicitante asociado al usuario 
    @GetMapping("/solicitante/{usuarioId}")
    public ResponseEntity<?> obtenerSolicitantePorUsuario(@PathVariable Long usuarioId) {
        Solicitante solicitante = solicitudService.obtenerSolicitantePorUsuario(usuarioId);
        if (solicitante == null) {
            return ResponseEntity.badRequest().body("Solicitante no encontrado para este usuario");
        }
        return ResponseEntity.ok(solicitante);
    }

    // ver la convocatoria abierta
    @GetMapping("/convocatoria-abierta")
    public ResponseEntity<?> obtenerConvocatoriaAbierta() {
        Convocatoria convocatoria = solicitudService.obtenerConvocatoriaAbierta();
        if (convocatoria == null) {
            return ResponseEntity.badRequest().body("No hay convocatoria abierta");
        }
        return ResponseEntity.ok(convocatoria);
    }
    // ver ofertas
    @GetMapping("/ofertas")
    public ResponseEntity<List<Oferta>> obtenerOfertas(@RequestParam Long convocatoriaId) {
        return ResponseEntity.ok(solicitudService.obtenerOfertasPorConvocatoria(convocatoriaId));
    }

    //ver mi solicitud 
    @GetMapping("/ver-solicitud/{usuarioId}")
    public ResponseEntity<?> obtenerVerSolicitud(@PathVariable Long usuarioId) {
        Solicitud solicitud = solicitudService.obtenerSolicitudPorUsuario(usuarioId);

        if (solicitud == null) {
            return ResponseEntity.badRequest().body("No existe ninguna solicitud para este usuario");
        }

        return ResponseEntity.ok(solicitud);
    }

    // crear solicitud
    @PostMapping
    public ResponseEntity<?> crearSolicitud(@RequestBody Solicitud solicitud) {
        try {
            Solicitud nuevaSolicitud = solicitudService.crearSolicitud(solicitud);
            return ResponseEntity.ok(nuevaSolicitud);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}