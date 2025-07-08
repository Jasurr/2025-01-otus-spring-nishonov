import React from 'react';
import ReactDOM from 'react-dom/client'; // Use client for React 18
import { BrowserRouter } from 'react-router-dom'; // Fix import
import App from './components/App';
import './styles/index.css';

const root = document.getElementById('root');

ReactDOM.createRoot(root).render(
    <BrowserRouter>
        <App />
    </BrowserRouter>
);