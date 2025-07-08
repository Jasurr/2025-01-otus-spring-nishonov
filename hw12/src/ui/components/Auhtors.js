import React from "react";

export default class Authors extends React.Component {
    constructor() {
        super();
        this.state = {authors: []};

    }

    componentDidMount() {
        fetch('/api/v1/authors', {
            headers: {
                'Content-Type': 'application/json', // Optional, include if the API expects JSON
                'Authorization': `Bearer ${localStorage.getItem('token')}` // Add Bearer token
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to fetch authors');
                }
                return response.json();
            })
            .then(authors => this.setState({ authors }))
            .catch(error => {
                console.error('Error fetching authors:', error);
                // Optionally handle the error (e.g., show a message to the user)
            });
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