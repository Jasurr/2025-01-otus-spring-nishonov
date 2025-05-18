import React from "react";

export default class Authors extends React.Component {
    constructor() {
        super();
        this.state = { authors: [] };

    }

    componentDidMount() {
        // fetch('/api/v1/authors')
        //     .then(response => response.json())
        //     .then(authors => this.setState({ authors }));
    }

    render() {
        return (
            <div className="authors">
                <h2>Authors</h2>
                <ul>
                    {this.state.authors.map(author => (
                        <li key={author.id}>{author.fullName}</li>
                    ))}
                </ul>
            </div>
        );
    }
}