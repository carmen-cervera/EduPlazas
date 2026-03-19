import axios from 'axios'

const BASE_URL = 'http://localhost:8080/solicitudes'

export const obtenerSolicitantePorUsuario = (usuarioId) =>
  axios.get(`${BASE_URL}/solicitante/${usuarioId}`)

export const obtenerConvocatoriaAbierta = () =>
  axios.get(`${BASE_URL}/convocatoria-abierta`)

export const obtenerOfertas = (convocatoriaId) =>
  axios.get(`${BASE_URL}/ofertas`, { params: { convocatoriaId } })

export const obtenerVerSolicitud = (usuarioId) =>
  axios.get(`${BASE_URL}/ver-solicitud/${usuarioId}`)

export const crearSolicitud = (solicitud) =>
  axios.post(`${BASE_URL}`, solicitud)