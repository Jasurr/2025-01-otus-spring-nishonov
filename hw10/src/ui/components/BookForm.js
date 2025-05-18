import React from 'react'
import {Link} from "react-router";

export const BookForm = () => {
    const [title, setTitle] = React.useState('');
    const [author, setAuthor] = React.useState('');
    const [genres, setGenres] = React.useState([]);
    const [message, setMessage] = React.useState('');
    const [authors, setAuthors] = React.useState([]);
    const [genresList, setGenresList] = React.useState([]);

    // Fetch authors and genres from the API
    React.useEffect(() => {
        // Fetch authors and genres logic here
        fetch('/api/authors')
            .then(response => response.json())
            .then(data => {
                setAuthors(data.authors);
            })
            .catch(error => console.error('Error fetching authors:', error));
        fetch('/api/genres')
            .then(response => response.json())
            .then(data => {
                setGenresList(data.genres);
            })
            .catch(error => console.error('Error fetching genres:', error));
    }, []);

    const handleSubmit = (event) => {
        event.preventDefault();
        // Add book logic here
        setMessage('Book added successfully!');
    };

    return (
        <div className="book-add">
            <h2>Add a New Book</h2>
            <form onSubmit={handleSubmit}>
                <label htmlFor="title">Title</label>
                <input type="text" id="title" value={title} onChange={(e) => setTitle(e.target.value)}
                       placeholder="Enter book title" required/>

                <label htmlFor="author">Author</label>
                <select id="author" value={author} onChange={(e) => setAuthor(e.target.value)} required>
                    <option value="">Select an author</option>
                    {
                        authors?.map((author) => (
                        <option key={author.id} value={author.id}>{author.name}</option>
                    ))}
                </select>

                <label htmlFor="genres">Genres</label>
                <select id="genres" value={genres}
                        onChange={(e) => setGenres([...e.target.selectedOptions].map(option => option.value))} multiple
                        required>
                    {genresList.map((genre) => (
                        <option key={genre.id} value={genre.id}>{genre.name}</option>
                    ))}
                </select>

                <div className="buttons">
                    <button type="submit">Save</button>
                    <Link to="/" className={"button-cancel"}>Cancel</Link>
                </div>
            </form>
            {message && <p>{message}</p>}
        </div>
    );
}