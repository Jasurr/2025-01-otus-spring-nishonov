import React from 'react'
import ReactDOM from 'react-dom'
import {BrowserRouter} from "react-router";
import App from './components/App'

import './styles/index.css';


const root = document.getElementById("root");

ReactDOM.createRoot(root).render(
    <BrowserRouter>
        <App />
    </BrowserRouter>
);