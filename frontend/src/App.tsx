// src/App.tsx
import "./App.css";
import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import { UserProvider } from "./context/UserContext";
import Navbar from "./components/navbar/Navbar";
// import ScrollToTop from "./components/scrollToTop/ScrollToTop";
import Home from "./pages/home/Home";
import Login from "./pages/login/Login";
import Account from "./pages/account/Account";
import Dashboard from "./pages/dashboard/Dashboard";
import Course from "./pages/course/Course";

const App: React.FC = () => {
  return (
    <UserProvider>
      <Router>
        {/* <ScrollToTop /> */}
        <div className="appContainer">
          <Navbar />
          <Routes>
            <Route path="/" element={<Home />} />
            <Route path="/login" element={<Login />} />
            <Route path="/account" element={<Account />} />
            <Route path="/dashboard" element={<Dashboard />} />
            <Route path="/course/:courseId" element={<Course />} />
          </Routes>
        </div>
      </Router>
    </UserProvider>
  );
};

export default App;
