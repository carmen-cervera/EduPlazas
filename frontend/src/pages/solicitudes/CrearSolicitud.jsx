import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import {obtenerSolicitantePorUsuario, obtenerConvocatoriaAbierta, obtenerOfertas, crearSolicitud} from '../../services/solicitudService'
import styles from './CrearSolicitud.module.css'
import logo from '../../assets/LogoPequeño_FondoAzul_SinGorro.png'
import avatar from '../../assets/avatar.png'


function CrearSolicitud() {
  const navigate = useNavigate()
  const usuario = JSON.parse(localStorage.getItem('usuario'))

  const [solicitante, setSolicitante] = useState(null)
  const [convocatoria, setConvocatoria] = useState(null)
  const [ofertas, setOfertas] = useState([])
  const [prioridad1, setPrioridad1] = useState('')
  const [prioridad2, setPrioridad2] = useState('')
  const [prioridad3, setPrioridad3] = useState('')
  const [mensaje, setMensaje] = useState('')
  const [error, setError] = useState('')

  useEffect(() => {
    cargarDatos()
  }, [])


  const cargarDatos = async () => {
    try {
      setError('')

      const resSolicitante = await obtenerSolicitantePorUsuario(usuario.id)
      setSolicitante(resSolicitante.data)

      const resConvocatoria = await obtenerConvocatoriaAbierta()
      setConvocatoria(resConvocatoria.data)

      const resOfertas = await obtenerOfertas(resConvocatoria.data.id)
      console.log('resOfertas completa:', resOfertas)
      console.log('resOfertas.data:', resOfertas.data)

      let datos = resOfertas.data
      if (typeof datos === 'string') {
        datos = JSON.parse(datos)
    }
      if (Array.isArray(resOfertas.data)) {
        const ofertasLimpias = resOfertas.data.map((oferta) => ({
          id: oferta.id,
          grado: oferta.grado,
          universidadNombre: oferta.universidad?.nombre || ''
        }))

        setOfertas(ofertasLimpias)
        setError('')
      } else {
        setOfertas([])
        setError('No se han podido cargar las ofertas correctamente')
      } 

    } catch (err) {
      setError(err.response?.data || 'Error al cargar los datos')
      setOfertas([])
    }
  }


  const handleEnviarSolicitud = async () => {
    try {
      setError('')
      setMensaje('')

      const idsSeleccionados = [prioridad1, prioridad2, prioridad3]
        .filter((id) => id !== '')

      if (idsSeleccionados.length === 0) {
        setError('Debes seleccionar al menos una opción')
        return
      }

      const idsUnicos = new Set(idsSeleccionados)
      if (idsUnicos.size !== idsSeleccionados.length) {
        setError('No puedes repetir el mismo grado en varias prioridades')
        return
      }

      const solicitud = {
        solicitante: { id: solicitante.id },
        convocatoria: { id: convocatoria.id },
        preferencias: idsSeleccionados.map((id) => ({ id: Number(id) }))
      }

      await crearSolicitud(solicitud)

      setMensaje('Solicitud enviada correctamente')
      setPrioridad1('')
      setPrioridad2('')
      setPrioridad3('')
    } catch (err) {
      setError(err.response?.data || 'Error al enviar la solicitud')
    }
  }


  const cerrarSesion = () => {
     localStorage.removeItem('usuario')
     navigate('/')
  }


  const renderOpciones = () => (
    <>
      <option value="">Selecciona un grado</option>
      {ofertas.map((oferta) => (
        <option key={oferta.id} value={oferta.id}>
          {oferta.grado} 
        </option>
      ))}
    </>
  )

    return (
    <div className={styles.page}>
      <header className={styles.header}>
        <img
          src={logo}
          alt="EduPlazas"
          className={styles.logoImg}
          onClick={() => navigate('/')}
        />
        <h1 className={styles.tituloHeader}>Nueva solicitud</h1>
      </header>

      <div className={styles.content}>
        <aside className={styles.sidebar}>
          <div className={styles.userBox}>
            <img
              src={avatar}
              alt="EduPlazas"
              className={styles.avatar}
            />
            <p className={styles.email}>{usuario?.email}</p>
          </div>
          
          <button className={styles.button} onClick={() => navigate('/estudiante/inicio')}>
            Volver
          </button>


          <button className={styles.button} onClick={cerrarSesion}>
            Log out
          </button>
          

        </aside>

        <main className={styles.main}>
          <div className={styles.card}>
            <h2 className={styles.sectionTitle}>Selección</h2>

            {error && <p className={styles.error}>{error}</p>}
            {mensaje && <p className={styles.success}>{mensaje}</p>}

            <div className={styles.formulario}>
              
              {convocatoria && (
              <p className={styles.convocatoria}>
                <strong>Convocatoria abierta:</strong> {convocatoria.nombre}
              </p>
            )}
              <label className={styles.label}>Grado de prioridad 1:</label>
              <select
                className={styles.select}
                value={prioridad1}
                onChange={(e) => setPrioridad1(e.target.value)}
              >
                {renderOpciones()}
              </select>

              <label className={styles.label}>Grado de prioridad 2:</label>
              <select
                className={styles.select}
                value={prioridad2}
                onChange={(e) => setPrioridad2(e.target.value)}
              >
                {renderOpciones()}
              </select>

              <label className={styles.label}>Grado de prioridad 3:</label>
              <select
                className={styles.select}
                value={prioridad3}
                onChange={(e) => setPrioridad3(e.target.value)}
              >
                {renderOpciones()}
              </select>
            </div>

            <div className={styles.footerButtons}>
              <button
                className={styles.primaryButton}
                onClick={handleEnviarSolicitud}
              >
                Enviar
              </button>
            </div>
          </div>
        </main>
      </div>
    </div>
  )
}
export default CrearSolicitud