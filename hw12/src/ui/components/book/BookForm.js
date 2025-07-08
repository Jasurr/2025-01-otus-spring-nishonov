import React, { useState, useEffect, useContext } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import {AuthContext} from "../AuthContext";

const BookForm = () => {
    const { token } = useContext(AuthContext); // Access token from AuthContext
    const [state, setState] = useState({
        bookId: '',
        title: '',
        author: '',
        genres: [],
        message: '',
        authors: [],
        genresList: [],
        loading: true
    });
    const navigate = useNavigate();
    const { id } = useParams();

    useEffect(() => {
        // Function to fetch book data for editing
        const fetchBook = async () => {
            if (!id) return;
            try {
                setState(prev => ({ ...prev, bookId: id, loading: true }));
                const response = await fetch(`/api/v1/books/${id}`, {
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token || localStorage.getItem('token')}` // Use token from context or localStorage
                    }
                });
                if (!response.ok) {
                    if (response.status === 401) {
                        navigate('/login'); // Redirect to login if unauthorized
                        throw new Error('Unauthorized access');
                    }
                    throw new Error('Failed to fetch book data');
                }
                const data = await response.json();
                setState(prev => ({
                    ...prev,
                    title: data.title || '',
                    author: data.author?.id || '',
                    genres: data.genres?.map(item => item.id) || [],
                    loading: false
                }));
            } catch (err) {
                console.error('Error loading book:', err);
                setState(prev => ({ ...prev, message: err.message, loading: false }));
            }
        };

        // Function to fetch authors
        const fetchAuthors = async () => {
            try {
                const response = await fetch('/api/v1/authors', {
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token || localStorage.getItem('token')}`
                    }
                });
                if (!response.ok) {
                    if (response.status === 401) {
                        navigate('/login');
                        throw new Error('Unauthorized access');
                    }
                    throw new Error('Failed to fetch authors');
                }
                const authors = await response.json();
                setState(prev => ({ ...prev, authors: Array.isArray(authors) ? authors : [] }));
            } catch (error) {
                console.error('Error fetching authors:', error);
                setState(prev => ({ ...prev, message: 'Failed to load authors', authors: [] }));
            }
        };

        // Function to fetch genres
        const fetchGenres = async () => {
            try {
                const response = await fetch('/api/v1/genres', {
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': `Bearer ${token || localStorage.getItem('token')}`
                    }
                });
                if (!response.ok) {
                    if (response.status === 401) {
                        navigate('/login');
                        throw new Error('Unauthorized access');
                    }
                    throw new Error('Failed to fetch genres');
                }
                const genres = await response.json();
                setState(prev => ({ ...prev, genresList: Array.isArray(genres) ? genres : [], loading: false }));
            } catch (error) {
                console.error('Error fetching genres:', error);
                setState(prev => ({ ...prev, message: 'Failed to load genres', genresList: [], loading: false }));
            }
        };

        // Execute fetches
        fetchBook();
        fetchAuthors();
        fetchGenres();
    }, [id, token, navigate]);

    const handleChange = (e) => {
        setState(prev => ({ ...prev, [e.target.id]: e.target.value }));
    };

    const handleGenresChange = (e) => {
        const selectedGenres = Array.from(e.target.selectedOptions).map(option => option.value);
        setState(prev => ({ ...prev, genres: selectedGenres }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        const { bookId, title, author, genres } = state;
        const genreIds = genres.map(item => ({ id: item }));
        const book = {
            id: bookId,
            title,
            author: { id: author },
            genres: genreIds
        };

        const url = '/api/v1/books';
        const method = bookId ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, {
                method,
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token || localStorage.getItem('token')}`
                },
                body: JSON.stringify(book)
            });
            if (!response.ok) {
                if (response.status === 401) {
                    navigate('/login');
                    throw new Error('Unauthorized access');
                }
                const error = await response.json();
                throw new Error(error.message || `Failed to ${bookId ? 'update' : 'add'} the book.`);
            }
            setState({
                bookId: '',
                title: '',
                author: '',
                genres: [],
                message: bookId ? 'Book updated successfully!' : 'Book added successfully!',
                authors: state.authors,
                genresList: state.genresList,
                loading: false
            });
            navigate('/');
        } catch (error) {
            setState(prev => ({ ...prev, message: error.message }));
        }
    };

    const { bookId, title, author, genres, message, authors, genresList, loading } = state;

    return (
        <div className="book-add">
            <h2>{bookId ? 'Edit Book' : 'Add a New Book'}</h2>
            {loading && <p>Loading...</p>}
            {message && <p>{message}</p>}
            {!loading && (
                <form onSubmit={handleSubmit}>
                    <label htmlFor="title">Title</label>
                    <input type="hidden" name="bookId" value={bookId} />
                    <input
                        type="text"
                        id="title"
                        value={title}
                        onChange={handleChange}
                        placeholder="Enter book title"
                        required
                    />

                    <label htmlFor="author">Author</label>
                    <select
                        id="author"
                        value={author}
                        onChange={handleChange}
                        required
                    >
                        <option value="">Select an author</option>
                        {authors.map((author) => (
                            <option key={author.id} value={author.id}>
                                {author.fullName}
                            </option>
                        ))}
                    </select>

                    <label htmlFor="genres">Genres</label>
                    <select
                        id="genres"
                        value={genres}
                        onChange={handleGenresChange}
                        multiple
                        required
                    >
                        {genresList.map((genre) => (
                            <option key={genre.id} value={genre.id}>
                                {genre.name}
                            </option>
                        ))}
                    </select>

                    <div className="buttons">
                        <button type="submit" className={"button-add"}>Save</button>
                        <Link to="/" className="button-cancel">Cancel</Link>
                    </div>
                </form>
            )}
        </div>
    );
};

export default BookForm;