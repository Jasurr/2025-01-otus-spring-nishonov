import React from 'react'
import {NavLink, Route, Routes} from "react-router";
import Authors from "./Auhtors";
import Genres from "./Genres";
import BookList from "./book/BookList";
import BookForm from "./book/BookForm";
import CommentList from "./comment/CommentList";
import CommentForm from "./comment/CommentForm";

export default class App extends React.Component {

    render() {
        return (
            <React.Fragment>
                <h1>Welcome to the Library</h1>
                <nav>
                    <NavLink to="/" end>
                        Home
                    </NavLink>
                    <NavLink to="/authors" end>
                        Authors
                    </NavLink>
                    <NavLink to="/genres">Genres</NavLink>
                </nav>
                <Routes>
                    <Route path="/" element={<BookList/>}/>
                    <Route path="/authors" element={<Authors/>}/>
                    <Route path="/genres" element={<Genres/>}/>
                    <Route path="/book/edit-book/:id" element={<BookForm />} />
                    <Route path={"/book/add-new"} element={<BookForm/>}/>
                    <Route path="/book/comments/:bookId" element={<CommentList />} />
                    <Route path="/book/comment/edit" element={<CommentForm />} />
                </Routes>
                <footer>
                    &copy; 2025 Library System
                </footer>
            </React.Fragment>
        )
    }
};
