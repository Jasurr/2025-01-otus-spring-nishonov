import React, { useState } from 'react';
import { NavLink, Route, Routes, useNavigate } from 'react-router-dom';
import Genres from './Genres';
import BookList from './book/BookList';
import BookForm from './book/BookForm';
import CommentList from './comment/CommentList';
import CommentForm from './comment/CommentForm';
import Login from './login/Login';
import { AuthContext } from './AuthContext';
import Authors from "./Auhtors";

const App = () => {
    const [isLogin, setIsLogin] = useState(!!localStorage.getItem('token')); // Check token on init
    const [userData, setUserData] = useState({});
    const navigate = useNavigate(); // Use hook in functional component

    // Login function to update state and store token
    const login = (userData) => {
        console.log('TOKEN', userData);
        localStorage.setItem('token', userData.token); // Assuming token is in userData
        setIsLogin(true);
        setUserData(userData);
    };

    // Logout function
    const logout = () => {
        localStorage.removeItem('token');
        setIsLogin(false);
        setUserData({});
        navigate('/login'); // Use navigate directly
    };

    return (
        <AuthContext.Provider value={{ login, logout, isLogin }}>
            <div>
                <h1>Welcome to the Library</h1>
                <nav>
                    <NavLink to="/" end>
                        Home
                    </NavLink>
                    <NavLink to="/authors" end>
                        Authors
                    </NavLink>
                    <NavLink to="/genres">Genres</NavLink>
                    {isLogin ? (
                        <button onClick={logout}>Logout</button>
                    ) : (
                        <NavLink to="/login">Login</NavLink>
                    )}
                </nav>
                <Routes>
                    <Route path="/" element={<BookList />} />
                    <Route path="/authors" element={<Authors />} />
                    <Route path="/genres" element={<Genres />} />
                    <Route path="/book/edit-book/:id" element={<BookForm />} />
                    <Route path="/book/add-new" element={<BookForm />} />
                    <Route path="/book/comments/:bookId" element={<CommentList />} />
                    <Route path="/book/comment/edit" element={<CommentForm />} />
                    <Route path="/login" element={<Login />} />
                </Routes>
                <footer>© 2025 Library System</footer>
            </div>
        </AuthContext.Provider>
    );
};

export default App;