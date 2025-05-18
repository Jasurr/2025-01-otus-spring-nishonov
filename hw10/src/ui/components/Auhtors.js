import React from "react";

export default class Authors extends React.Component {
    constructor() {
        super();
        this.state = {authors: []};

    }

    componentDidMount() {
        fetch('/api/v1/authors')
            .then(response => response.json())
            .then(authors => this.setState({authors}));
    }

    render() {
        return (
            <table>
                <thead>
                <tr>
                    <th>ID</th>
                    <th>Name</th>
                </tr>
                </thead>
                <tbody>
                {
                    this.state.authors.map(author => (
                        <tr key={author.id}>
                            <td>{author.id}</td>
                            <td>{author.fullName}</td>
                        </tr>
                    ))
                }
                </tbody>
            </table>
        );
    }
}