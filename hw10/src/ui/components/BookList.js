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
                                        <a href={`/book/delete/${book.id}`} className="button button-delete"
                                           onClick={() => confirm('Are you sure you want to delete this book?')}>Delete</a>

                                        <a href="javascript:void(0);"
                                           className="button button-comment open-comment-button"
                                           data-book-id={book.id} data-book-title={book.title}>
                                            Comments
                                        </a>
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
