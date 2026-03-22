import { useNavigate } from 'react-router-dom'
import styles from './UniversidadInicio.module.css'
import logo from '../../assets/LogoPequeño_FondoAzul_SinGorro.png'
import avatar from '../../assets/avatar.png'

function UniversidadInicio() {
  const navigate = useNavigate()
  const usuario = JSON.parse(localStorage.getItem('usuario'))

  const cerrarSesion = () => {
    localStorage.removeItem('usuario')
    navigate('/')
  }

  const esAdmin = usuario?.rol === 'ADMIN'

  return (
    <div className={styles.page}>
      <header className={styles.header}>
        <img
          src={logo}
          alt="EduPlazas"
          className={styles.logoImg}
          onClick={() => navigate('/')}
        />
        <h1 className={styles.tituloHeader}>
          {esAdmin ? 'Panel administrador / universidad' : 'Panel universidad'}
        </h1>
      </header>

      <div className={styles.content}>
        <aside className={styles.sidebar}>
          <div className={styles.userBox}>
            <img src={avatar} alt="" className={styles.avatar} />
            <p className={styles.email}>{usuario?.email}</p>
            {esAdmin && (
              <p className={styles.rolBadge}>Rol: administrador</p>
            )}
          </div>

          <button className={styles.button} onClick={cerrarSesion}>
            Log out
          </button>
        </aside>

        <div className={styles.card}>
          <h2 className={styles.cardTitle}>Bienvenid@</h2>
          <p className={styles.mainText}>
            {esAdmin
              ? 'Como administrador puedes usar el login de estudiantes para el área de estudiante y el de universidades para este panel.'
              : 'Aquí irá el panel de gestión de tu universidad.'}
          </p>
        </div>
      </div>
    </div>
  )
}

export default UniversidadInicio
