import React, { useState, useEffect, useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {AuthContext} from "../AuthContext";

const BookList = () => {
    const { token } = useContext(AuthContext); // Access token from AuthContext
    const [books, setBooks] = useState([]);
    const [error, setError] = useState('');
    const navigate = useNavigate();

    useEffect(() => {
        // Fetch books with Bearer token
        fetch('/api/v1/books', {
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token || localStorage.getItem('token')}` // Use token from context or localStorage
            }
        })
            .then(response => {
                if (!response.ok) {
                    if (response.status === 401) {
                        navigate('/login'); // Redirect to login if unauthorized
                        throw new Error('Unauthorized access');
                    }
                    throw new Error('Failed to fetch books');
                }
                return response.json();
            })
            .then(books => setBooks(books ? books : []))
            .catch(error => {
                console.error('Error fetching books:', error);
                setError(error.message);
            });
    }, [token, navigate]);

    const deleteBookById = (bookId) => {
        if (!window.confirm('Are you sure you want to delete this book?')) {
            return;
        }

        // Delete book with Bearer token
        fetch(`/api/v1/books/${bookId}`, {
            method: 'DELETE',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token || localStorage.getItem('token')}`
            }
        })
            .then(response => {
                if (!response.ok) {
                    if (response.status === 401) {
                        navigate('/login');
                        throw new Error('Unauthorized access');
                    }
                    return response.json().then(error => {
                        throw new Error(error.message || 'Failed to delete the book.');
                    });
                }
                setBooks(books.filter(book => book.id !== bookId));
                setError('');
            })
            .catch(error => {
                setError(error.message);
            });
    };

    return (
        <>
            {error && (
                <div style={{ color: 'red', textAlign: 'center' }}>
                    <p>{error}</p>
                </div>
            )}
            <div className="top-actions">
                <Link to="/book/add-new" className="button button-add">Add New Book</Link>
            </div>

            <div className="table-container">
                <table>
                    <thead>
                    <tr>
                        <th>ID</th>
                        <th>Title</th>
                        <th>Author</th>
                        <th>Genres</th>
                        <th>Actions</th>
                    </tr>
                    </thead>
                    <tbody>
                    {books.map(book => (
                        <tr key={book.id}>
                            <td>{book.id}</td>
                            <td>{book.title}</td>
                            <td>{book.author.fullName}</td>
                            <td>
                                {book.genres.map((genre, index) => (
                                    <span key={index}>
                      {genre.name}
                                        {index < book.genres.length - 1 ? ', ' : ''}
                    </span>
                                ))}
                            </td>
                            <td className="action-buttons">
                                <Link to={`/book/edit-book/${book.id}`} className="button button-edit">
                                    Edit
                                </Link>
                                <a
                                    href="#"
                                    className="button button-delete"
                                    onClick={() => deleteBookById(book.id)}
                                >
                                    Delete
                                </a>
                                <Link
                                    to={`/book/comments/${book.id}`}
                                    className="button button-comment open-comment-button"
                                >
                                    Comments
                                </Link>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </>
    );
};

export default BookList;