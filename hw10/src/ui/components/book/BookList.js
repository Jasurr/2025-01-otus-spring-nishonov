import React from 'react'
import {Link} from "react-router";

export default class BookList extends React.Component {
    constructor() {
        super();
        this.state = {books: []};
    }

    componentDidMount() {
        fetch('/api/v1/books')
            .then(response => response.json())
            .then(books => this.setState({books}));
    }

    deleteBookById(bookId) {
        if (window.confirm('Are you sure you want to delete this book?')) {
            fetch(`/api/v1/books/${bookId}`, {
                method: 'DELETE'
            })
                .then(response => {
                    if (response.ok) {
                        this.setState(prevState => ({
                            books: prevState.books.filter(book => book.id !== bookId)
                        }));
                    } else {
                        return response.json().then(error => {
                            throw new Error(error.message || 'Failed to delete the book.');
                        });
                    }
                })
                .catch(error => {
                    alert(error.message);
                });
        }
    }

    render() {
        return (
            <>
                <div className="top-actions">
                    <Link to="/book/add-new" className={"button button-add"}>Add New Book</Link>
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
                        {
                            this.state.books.map(book => (
                                <tr key={book.id}>
                                    <td>{book.id}</td>
                                    <td>{book.title}</td>
                                    <td>{book.author.fullName}</td>
                                    <td>
                                        {book.genres.map((genre, index) => (
                                            <span
                                                key={index}>{genre.name}{index < book.genres.length - 1 ? ', ' : ''}</span>
                                        ))}
                                    </td>
                                    <td className="action-buttons">
                                        <Link to={`/book/edit-book/${book.id}`} className="button button-edit">Edit</Link>
                                        <a href="#" className="button button-delete"
                                           onClick={() => this.deleteBookById(book.id)}>
                                            Delete</a>

                                        <Link to={"/book/comments/" + book.id}
                                              className="button button-comment open-comment-button">
                                            Comments
                                        </Link>
                                    </td>
                                </tr>
                            ))
                        }
                        </tbody>
                    </table>
                </div>
            </>
        );
    }
}
