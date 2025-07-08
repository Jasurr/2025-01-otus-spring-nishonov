import React, { useState, useContext } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../AuthContext';

const Login = () => {
    const { login } = useContext(AuthContext);
    const [credentials, setCredentials] = useState({ username: '', password: '' });
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleChange = (e) => {
        setCredentials({ ...credentials, [e.target.name]: e.target.value });
    };

    const handleSubmitLogin = async (e) => {
        e.preventDefault();
        setError('');
        try {
            const response = await fetch('/api/v1/auth/login', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(credentials),
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || 'Login failed. Please try again.');
            }

            const token = await response.text();
            const userData = {
                token
            }
            login(userData); // Call login with API response data
            navigate('/'); // Redirect to home after successful login
        } catch (err) {
            console.error('Login error:', err);
            setError(err.message || 'Invalid credentials');
        }
    };

    return (
        <div>
            <h2>Login</h2>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            <form onSubmit={handleSubmitLogin}>
                <div>
                    <label>Username:</label>
                    <input
                        type="text"
                        name="username"
                        value={credentials.username}
                        onChange={handleChange}
                        required
                    />
                </div>

                <div>
                    <label>Password:</label>
                    <input
                        type="password"
                        name="password"
                        value={credentials.password}
                        onChange={handleChange}
                        required
                    />
                </div>
                <button type="submit"
                        style={{
                            display: 'inline-block',
                            padding: '10px 20px',
                            margin: '5px 0',
                            color: 'white',
                            border: 'none',
                            borderRadius: '6px',
                            backgroundColor: '#2ecc71',
                            cursor: 'pointer',
                            transition: 'background-color 0.3s ease'
                        }}
                >Login</button>
            </form>
        </div>
    );
};

export default Login;