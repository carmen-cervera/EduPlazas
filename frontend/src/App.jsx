import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Home from './pages/Home'
import Login from './pages/auth/Login'
import RegistroEstudiante from './pages/auth/RegistroEstudiante'
import RegistroUniversidad from './pages/auth/RegistroUniversidad'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/estudiantes/login" element={<Login rol="ESTUDIANTE" />} />
        <Route path="/universidades/login" element={<Login rol="UNIVERSIDAD" />} />
        <Route path="/registro/estudiante" element={<RegistroEstudiante />} />
        <Route path="/registro/universidad" element={<RegistroUniversidad />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App