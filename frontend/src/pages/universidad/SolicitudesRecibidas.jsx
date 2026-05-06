import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import styles from './UniversidadInicio.module.css'
import logo from '../../assets/LogoPequeño_FondoBlanco_SinGorro.png'

function SolicitudesRecibidas() {
    const navigate = useNavigate()
    const usuario = JSON.parse(localStorage.getItem('usuario'))
    const [solicitudes, setSolicitudes] = useState([])

    useEffect(() => {
        // Usamos el ID de la universidad que viene en tu objeto 'usuario'
        const univId = usuario?.universidad?.id
        if (univId) {
            fetch(`http://localhost:8080/solicitudes/universidad/recibidas/${univId}`)
                .then(res => res.json())
                .then(data => setSolicitudes(data))
                .catch(err => console.error("Error al cargar solicitudes:", err))
        }
    }, [usuario])

    const cerrarSesion = () => {
        localStorage.removeItem('usuario')
        navigate('/')
    }

    return (
        <div className={styles.page}>
            <header className={styles.header}>
                <img
                    src={logo}
                    alt="EduPlazas"
                    className={styles.logoImg}
                    onClick={() => navigate('/')}
                />
                <h1 className={styles.tituloHeader}>Solicitudes recibidas</h1>
            </header>

            <div className={styles.content}>
                <aside className={styles.sidebar}>
                    <div className={styles.userBox}>
                        <p className={styles.email}>{usuario?.email}</p>
                        <p className={styles.universidad}>{usuario?.universidad?.nombre}</p>
                    </div>

                    <div className={styles.menu}>
                        <button className={styles.button} onClick={() => navigate('/universidad/publicar-offer')}>
                            Publicar oferta
                        </button>
                        <button className={styles.button} onClick={() => navigate('/universidad/mis-ofertas')}>
                            Mis ofertas
                        </button>
                        <button className={styles.button} onClick={() => navigate('/universidad/inicio')}>
                            Volver al inicio
                        </button>
                    </div>

                    <button className={styles.logoutBtn} onClick={cerrarSesion}>
                        Log out
                    </button>
                </aside>

                <main className={styles.main}>
                    {/* Usamos la clase tableCard que tienes en tu CSS */}
                    <div className={styles.tableCard}>
                        <h2 className={styles.cardTitle}>Listado de Estudiantes</h2>
                        <table className={styles.table}>
                            <thead>
                                <tr>
                                    <th>Estudiante</th>
                                    <th>Grado</th>
                                    <th>Preferencia</th>
                                    <th>Estado</th>
                                </tr>
                            </thead>
                            <tbody>
                                {solicitudes.length > 0 ? (
                                    solicitudes.map((s) => (
                                        <tr key={s.idSolicitud}>
                                            <td>{s.nombreEstudiante}</td>
                                            <td>{s.nombreGrado}</td>
                                            <td><strong>{s.ordenPreferencia}º</strong></td>
                                            <td className={styles.mainText}>{s.estado}</td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td colSpan="4" style={{ textAlign: 'center', padding: '20px' }}>
                                            No se han recibido solicitudes todavía.
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </main>
            </div>
        </div>
    )
}

export default SolicitudesRecibidas