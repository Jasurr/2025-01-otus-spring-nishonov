import React from 'react'
import {Link} from "react-router";
import withRouter from "../util/withRouter";

class BookForm extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            bookId: '',
            title: '',
            author: '',
            genres: [],
            message: '',
            authors: [],
            genresList: []
        };
    }

    componentDidMount() {
        const { id } = this.props.params;

        console.log("BookID", id)
        if (id) {
            this.setState({bookId: id});
            // Edit mode — fetch book data by ID
            fetch(`/api/books/${id}`)
                .then(res => res.json())
                .then(data => {
                    console.log('GET_BOOK_BY_ID', data)
                    this.setState({
                        title: data.title,
                        author: data.author.id,
                        genres: data.genres.map(item => item.id)
                    });
                })
                .catch(err => console.error("Error loading book:", err));
        }
        // load authors
        fetch('/api/v1/authors')
            .then(response => response.json())
            .then(authors => {
                this.setState({authors});
            })
            .catch(error => console.error('Error fetching authors:', error));
        // load genres
        fetch('/api/v1/genres')
            .then(response => response.json())
            .then(genres => {
                this.setState({genresList: genres});
            })
            .catch(error => console.error('Error fetching genres:', error));
    }

    handleChange = (e) => {
        this.setState({[e.target.id]: e.target.value});
    };

    handleGenresChange = (e) => {
        const selectedGenres = Array.from(e.target.selectedOptions).map(option => option.value);
        this.setState({genres: selectedGenres});
    };

    handleSubmit = (e) => {
        e.preventDefault();
        const {bookId, title, author, genres} = this.state;
        const genereIds = genres.map(item => {
            return {
                id: item
            }
        })
        const book = {
            id: bookId,
            title,
            author: {
                id: author
            },
            genres: genereIds
        }
        fetch(`/api/v1/books/add`, {
            method: "POST",
            headers: {'Content-Type': 'application/json'},
            body: JSON.stringify(book)
        }).then(() => {
            this.setState({bookId: ''})
            this.setState({title: ''})
            this.setState({author: ''})
            this.setState({genres: ''})
        });

        this.setState({message: 'Book added successfully!'});
    };

    render() {
        const {bookId, title, author, genres, message, authors, genresList} = this.state;

        return (
            <div className="book-add">
                <h2>Add a New Book</h2>
                <form onSubmit={this.handleSubmit}>
                    <label htmlFor="title">Title</label>
                    <input type="hidden" name="bookId" value={bookId}/>
                    <input
                        type="text"
                        id="title"
                        value={title}
                        onChange={this.handleChange}
                        placeholder="Enter book title"
                        required
                    />

                    <label htmlFor="author">Author</label>
                    <select
                        id="author"
                        value={author}
                        onChange={this.handleChange}
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
                        onChange={this.handleGenresChange}
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
                        <button type="submit">Save</button>
                        <Link to="/" className="button-cancel">Cancel</Link>
                    </div>
                </form>
                {message && <p>{message}</p>}
            </div>
        );
    }
}

export default withRouter(BookForm);